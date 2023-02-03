package org.enzopapiro.marketprice.util.parsing.text;

/**
 * This class assists in keeping track of a char sequence index as we progress through parsing fields
 * encoded inside the char sequence.
 */
public class Position {
    int pos;
    public Position(){
    }

    public int get(){
        return pos;
    }

    public Position set(int pos) {
        this.pos = pos;
        return this;
    }

    public Position increment(){
        pos++;
        return this;
    }

    public Position reset(){
        pos=0;
        return this;
    }
}
