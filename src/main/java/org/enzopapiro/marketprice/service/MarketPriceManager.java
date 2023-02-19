package org.enzopapiro.marketprice.service;

import org.agrona.concurrent.SnowflakeIdGenerator;
import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.scale.Scales;
import org.enzopapiro.marketprice.config.MarketPriceGatewayConfiguration;
import org.enzopapiro.marketprice.domain.Price;
import org.enzopapiro.marketprice.domain.Symbol;
import org.enzopapiro.marketprice.util.concurrency.AtomicReadWriteSynchroniser;

import java.nio.channels.ScatteringByteChannel;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class MarketPriceManager {
    class PriceEntry {
        long messageId;

        Price price;

        AtomicReadWriteSynchroniser sync;

        PriceEntry(MarketPriceGatewayConfiguration config,Price price){
            this.price = price;
            this.sync = new AtomicReadWriteSynchroniser(config.getCacheWriteWinProbability());
        }

        public Price getPrice() {
            return price;
        }

        public void setMessageId(long messageId) {
            this.messageId = messageId;
        }

        public long getMessageId() {
            return messageId;
        }

        public boolean startUpdate(){
            while(!sync.tryWrite());
            return true;
        }

        public void complete(){
            sync.done();
        }

        public boolean startRead(){
            while(!sync.tryRead());
            return true;
        }

    }
    private final ConcurrentMap<Symbol, PriceEntry> cache;
    private final SnowflakeIdGenerator idGenerator;
    private final ThreadLocal<Price> tlPrice = ThreadLocal.withInitial(Price::new);
    private final MarketPriceAction[] actions;

    public MarketPriceManager(MarketPriceGatewayConfiguration config, MarketPriceAction[] actions){
        List<String> symbols = config.getSubscriptionSymbols();
        this.cache = new ConcurrentHashMap<>(symbols.size());
        for(String s:symbols){
            Symbol symbol = new Symbol(s);
            this.cache.put(symbol,new PriceEntry(config,new Price().setSymbol(symbol)));
        }
        idGenerator = new SnowflakeIdGenerator(config.getIdGeneratorProcessId());
        this.actions = actions;
    }

    private PriceEntry get(Symbol symbol){
        return cache.get(symbol);
    }

    public void update(long messageId,Symbol symbol,long bid, long ask, int priceScale,long timestamp){

        PriceEntry pe = get(symbol);

        // just incase the system resend the same message again after processing
        // ignore as we don't want stale rates.
        //
        try {
            pe.startUpdate();
            if (pe != null && pe.messageId < messageId ) {
                pe.setMessageId(messageId);
                Price p = pe.getPrice();
                p.setId(idGenerator.nextId());
                p.setTimestamp(timestamp);
                applyBidAskMargin(p, bid, ask, priceScale);
                publish(PublishReason.Update,p);
            }
        }finally{
            pe.complete();
        }
    }

    public void request(Symbol symbol){
        Price outboundPrice = tlPrice.get();
        PriceEntry pe = get(symbol);
        if(pe!=null ){
            try {
                pe.startRead();
                long currentId = pe.getMessageId();
                Price cached = pe.getPrice();
                outboundPrice.setId(cached.getId());
                outboundPrice.setSymbol(cached.getSymbol());
                outboundPrice.setBid(cached.getBid().getValue(), cached.getBid().getScale());
                outboundPrice.setAsk(cached.getAsk().getValue(), cached.getAsk().getScale());
                outboundPrice.setTimestamp(cached.getTimestamp());
                publish(PublishReason.Request,outboundPrice);
            }finally{
                pe.complete();
            }
        }
    }

    public static int MAX_MARGIN_SCALE = 10;
    public static double BID_MARGIN = 0.999D;
    public static double ASK_MARGIN = 1.001D;
    private static long MAX_SCALE_DIVISOR = (long)Math.pow(10,MAX_MARGIN_SCALE);

    public static Price applyBidAskMargin(Price price, long bid, long ask, int priceScale) {
        ScaleMetrics sm = Scales.getScaleMetrics(MAX_MARGIN_SCALE);
        DecimalArithmetic arith = sm.getDefaultArithmetic();
        long minusMargin = arith.fromDouble(BID_MARGIN);
        long marginBid = arith.multiplyByLong(bid,minusMargin);
        marginBid = arith.divideByLong(marginBid,MAX_SCALE_DIVISOR);
        price.setBid(marginBid,priceScale);

        long plusMargin = arith.fromDouble(ASK_MARGIN);
        long marginAsk = arith.multiplyByLong(ask,plusMargin);
        marginAsk = arith.divideByLong(marginAsk,MAX_SCALE_DIVISOR);
        price.setAsk(marginAsk,priceScale);

        return price;
    }

    public void publish(PublishReason reason,Price price){
        for (MarketPriceAction action : actions) {
            action.onPricePublish(reason,price);
        }
    }
}
