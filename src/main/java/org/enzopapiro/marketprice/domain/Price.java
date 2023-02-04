package org.enzopapiro.marketprice.domain;

import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.scale.Scales;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class Price {
    private volatile long id;
    private volatile Symbol symbol;
    private volatile Decimal bid;
    private volatile Decimal ask;
    private volatile long  timestamp;

    public Price(){
        this.bid = new Decimal();
        this.ask = new Decimal();
    }
    public Price setSymbol(Symbol symbol) {
        this.symbol = symbol;
        return this;
    }

    public Symbol getSymbol() {
        return symbol;
    }

    public Price setId(long id){
        this.id = id;
        return this;
    }

    public long getId() {
        return id;
    }

    public Price setTimestamp(long timestamp){
        this.timestamp = timestamp;
        return this;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public ZonedDateTime getTimestampAsZonedDateTime() {
        return Instant.ofEpochMilli(timestamp).atZone(ZoneId.of("GMT"));
    }

    public Price setBid(long value, int scale){
        this.bid.setLong(value);
        this.bid.setScale(scale);
        return this;
    }

    public Decimal getBid() {
        return bid;
    }

    public Price setAsk(long value, int scale){
        this.ask.setLong(value);
        this.ask.setScale(scale);
        return this;
    }

    public Decimal getAsk() {
        return ask;
    }

    @Override
    public String toString() {

        ScaleMetrics sm = Scales.getScaleMetrics(this.bid.getScale());
        DecimalArithmetic arith = sm.getDefaultArithmetic();

        String bidString = arith.toString(this.bid.getValue());
        String askString = arith.toString(this.ask.getValue());

        return String.format("%d %s %s/%s %s",this.id,this.symbol!=null?symbol.getCodeString():"",bidString,askString,getTimestampAsZonedDateTime());
    }
}
