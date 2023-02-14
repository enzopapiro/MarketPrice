package org.enzopapiro.marketprice.util.parsing.text.symbol;

import org.agrona.collections.Long2ObjectHashMap;
import org.enzopapiro.marketprice.domain.Symbol;
import org.enzopapiro.marketprice.util.parsing.text.Position;
import org.enzopapiro.marketprice.util.parsing.text.string.String8LongEncoder;

import java.nio.CharBuffer;

public class SymbolParser {

    private final CharBuffer receiveBuffer;

    private final Long2ObjectHashMap<Symbol> symbolMap;

    public SymbolParser() {
        receiveBuffer = CharBuffer.allocate(6);
        symbolMap = new Long2ObjectHashMap<>(100,0.75f,false);
    }

    public static char normaliseChar(char c) {
        if(c >= 'A' && c <= 'Z' ){
            return c;
        } else if ( (c >= 'a' && c <= 'z') ){
            return (char)(c -('a'-'A'));
        }
        return 0;
    }

    public long getSymbolCode(CharSequence symbol){
        return String8LongEncoder.stringToLong(symbol);
    }

    public Symbol constructSymbol(long symbolCode){
        return new Symbol(symbolCode);
    }


    public Symbol parse(CharSequence charSequence, Position currentPosition, char delim) {

        receiveBuffer.clear();

        for(int i=currentPosition.get();i<charSequence.length();i++){
            char c = charSequence.charAt(i);
            if(c == delim){
                currentPosition.set(i);
                break;
            }
            c = normaliseChar(charSequence.charAt(i));
            if(c!=0&&receiveBuffer.hasRemaining()) {
                receiveBuffer.put(c);
            }
        }

        receiveBuffer.flip();

        long symbolCode = getSymbolCode(receiveBuffer);

        return symbolMap.computeIfAbsent(symbolCode,this::constructSymbol);
    }
}
