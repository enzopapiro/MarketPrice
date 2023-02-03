package org.enzopapiro.marketprice.domain;

import org.enzopapiro.marketprice.util.parsing.text.string.String8LongEncoder;

import java.util.Objects;

public class Symbol {
    private String codeString;
    private long code;
    public Symbol(CharSequence symbol){
        this.code = String8LongEncoder.stringToLong(symbol);
        this.codeString = String8LongEncoder.longToString(code);
    }

    public Symbol(long code){
        this.codeString = String8LongEncoder.longToString(code);
        this.code = code;
    }

    public long getCode() {
        return code;
    }

    public String getCodeString() {
        return codeString;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Symbol symbol = (Symbol) o;
        return code == symbol.code;
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }
}
