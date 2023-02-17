package org.enzopapiro.marketprice.domain;

import java.util.Objects;

public class Decimal {
    private long value;
    private int scale;

    public Decimal setScale(int scale) {
        this.scale = scale;
        return this;
    }

    public Decimal setLong(long value){
        this.value = value;
        return this;
    }

    public int getScale() {
        return scale;
    }

    public long getValue() {
        return value;
    }

    public Decimal reset(){
        this.value = 0;
        this.scale = 0;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Decimal decimal64 = (Decimal) o;
        return value == decimal64.value && scale == decimal64.scale;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, scale);
    }

    @Override
    public String toString() {
        return String.format("Decimal64 v:%d s:%d",value,scale);
    }
}
