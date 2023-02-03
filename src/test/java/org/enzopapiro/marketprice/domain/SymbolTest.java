package org.enzopapiro.marketprice.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SymbolTest {
    @Test
    public void testSymbol(){

        Symbol symbol = new Symbol("EURUSD");

        long symbolCode = symbol.getCode();

        Symbol symbol2 = new Symbol(symbolCode);

        Assertions.assertEquals("EURUSD",symbol2.getCodeString());
        Assertions.assertEquals(symbolCode,symbol2.getCode());
        Assertions.assertEquals(symbol,symbol2);
    }
}
