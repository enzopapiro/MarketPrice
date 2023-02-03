package org.enzopapiro.marketprice.util.parsing.symbol;

import org.enzopapiro.marketprice.util.parsing.text.symbol.SymbolParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SymbolParserTest {
    @Test
    public void testCaseNormalisation() {
        Assertions.assertEquals('A',SymbolParser.normaliseChar('A'));
        Assertions.assertEquals('A',SymbolParser.normaliseChar('a'));
        Assertions.assertEquals('Z',SymbolParser.normaliseChar('Z'));
        Assertions.assertEquals('Z',SymbolParser.normaliseChar('z'));
        Assertions.assertEquals( 0, SymbolParser.normaliseChar('/'));
        Assertions.assertEquals( 0, SymbolParser.normaliseChar(' '));
        Assertions.assertEquals( 0, SymbolParser.normaliseChar('@'));
    }
}
