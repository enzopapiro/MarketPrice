package org.enzopapiro.marketprice.service;

import org.agrona.concurrent.SnowflakeIdGenerator;
import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.scale.Scales;
import org.enzopapiro.marketprice.config.MarketPriceGatewayConfiguration;
import org.enzopapiro.marketprice.domain.Price;
import org.enzopapiro.marketprice.domain.Symbol;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class MarketPriceManager {

    enum PublishReason {
        Update,
        Request
    }

    class PriceEntry {
         long messageId;

         Price price;

        AtomicBoolean updateInProgress = new AtomicBoolean(false);

        PriceEntry(Price price){
            this.price = price;
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
            return updateInProgress.compareAndSet(false,true);
        }

        public void endUpdate(){
            updateInProgress.set(false);
        }

        public boolean notReadable(){
            return updateInProgress.get();
        }
    }
    private final ConcurrentMap<Symbol, PriceEntry> cache;

    private final SnowflakeIdGenerator idGenerator;

    private final ThreadLocal<Price> tlPrice = ThreadLocal.withInitial(Price::new);
    public MarketPriceManager(MarketPriceGatewayConfiguration config){
        List<String> symbols = config.getSubscriptionSymbols();
        cache = new ConcurrentHashMap<>(symbols.size());
        for(String s:symbols){
            Symbol symbol = new Symbol(s);
            cache.put(symbol,new PriceEntry(new Price().setSymbol(symbol)));
        }
        idGenerator = new SnowflakeIdGenerator(config.getIdGeneratorProcessId());
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
            if (pe != null && pe.messageId < messageId) {
                pe.setMessageId(messageId);
                Price p = pe.getPrice();
                p.setId(idGenerator.nextId());
                p.setTimestamp(timestamp);
                applyBidAskMargin(p, bid, ask, priceScale);
                publish(PublishReason.Update,p);
            }
        }finally{
            pe.endUpdate();
        }
    }

    public void request(Symbol symbol){
        Price outboundPrice = tlPrice.get();
        PriceEntry pe = get(symbol);
        if(pe!=null ){
            try {
                pe.startUpdate();
                long currentId = pe.getMessageId();
                Price cached = pe.getPrice();
                outboundPrice.setId(cached.getId());
                outboundPrice.setSymbol(cached.getSymbol());
                outboundPrice.setBid(cached.getBid().getValue(), cached.getBid().getScale());
                outboundPrice.setAsk(cached.getAsk().getValue(), cached.getAsk().getScale());
                outboundPrice.setTimestamp(cached.getTimestamp());
                publish(PublishReason.Request,outboundPrice);
            }finally{
                pe.endUpdate();
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
        if(reason==PublishReason.Request) {
            System.out.println(String.format("%s %s", reason, price.toString()));
        }
    }
}
