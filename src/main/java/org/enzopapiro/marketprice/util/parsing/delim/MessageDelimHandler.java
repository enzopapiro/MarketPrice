package org.enzopapiro.marketprice.util.parsing.delim;

public interface MessageDelimHandler {
    void onMessage(CharSequence seq, int start, int end);
}
