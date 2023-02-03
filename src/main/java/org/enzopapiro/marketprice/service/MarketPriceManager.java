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

public class MarketPriceManager {

    class PriceEntry {
        volatile long messageId;
        volatile Price price;

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
        if(pe!=null && pe.messageId < messageId){
            pe.setMessageId(messageId);
            Price p = pe.getPrice();
            p.setId(idGenerator.nextId());
            p.setTimestamp(timestamp);
            applyBidAskMargin(p,bid,ask,priceScale);
            publish(p);
        }
    }

    public void request(Symbol symbol){
        System.out.println(String.format("Incoming request ", symbol.getCodeString()));
        Price outboundPrice = tlPrice.get();
        PriceEntry pe = get(symbol);
        if(pe!=null ){
            long currentId = pe.getMessageId();
            Price cached = pe.getPrice();
            do{
                outboundPrice.setId(cached.getId());
                outboundPrice.setSymbol(cached.getSymbol());
                outboundPrice.setBid(cached.getBid().getValue(),cached.getBid().getScale());
                outboundPrice.setAsk(cached.getAsk().getValue(),cached.getAsk().getScale());
                outboundPrice.setTimestamp(cached.getTimestamp());

            }while(currentId!=pe.getMessageId());
            publish(outboundPrice);
        }
    }

    private void applyBidAskMargin(Price price,long bid,long ask,int priceScale){
        ScaleMetrics sm = Scales.getScaleMetrics(priceScale);
        DecimalArithmetic arith = sm.getDefaultArithmetic();

        long minusMargin = arith.fromDouble(0.999D);
        long marginBid = arith.multiplyByUnscaled(bid,minusMargin,priceScale);
        price.setBid(marginBid,priceScale);

        long plusMargin = arith.fromDouble(1.001D);
        price.setAsk(arith.multiplyByUnscaled(ask,plusMargin,priceScale),priceScale);
    }

    public void publish(Price price){
        System.out.println(price.toString());
    }
}
