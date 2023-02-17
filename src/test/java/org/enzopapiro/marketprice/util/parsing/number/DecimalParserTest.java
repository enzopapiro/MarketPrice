package org.enzopapiro.marketprice.util.parsing.number;

import org.enzopapiro.marketprice.domain.Decimal;
import org.enzopapiro.marketprice.util.parsing.text.Position;
import org.enzopapiro.marketprice.util.parsing.text.number.DecimalParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DecimalParserTest {

    @Test
    void testScaledDecimalParsing() {
        Decimal result1 = new Decimal();
        Position position = new Position();
        DecimalParser.parseNumberToDelim(" - 1234.123,", position.set(0), ',',result1);
        Assertions.assertEquals(new Decimal().setLong(-1234123).setScale(3),result1);

        Decimal result2 = new Decimal();
        DecimalParser.parseNumberToDelim("1234.123,", position.set(0), ',',result2);
        Assertions.assertEquals(new Decimal().setLong(-1234123).setScale(3),result1);

        Decimal result3 = new Decimal();
        DecimalParser.parseNumberToDelim("12 34.123", position.set(0), ',',result3);
        Assertions.assertEquals(new Decimal().setLong(1234123).setScale(3),result3);

        Decimal result4 = new Decimal();

        DecimalParser.parseNumberToDelim("1234123", position.set(0), ',',result4);
        Assertions.assertEquals(new Decimal().setLong(1234123).setScale(0),result4);

        Decimal result5 = new Decimal();
        DecimalParser.parseNumberToDelim("12 -34.123", position.set(0), ',',result5);
        Assertions.assertEquals(new Decimal().setLong(1234123).setScale(3),result5);

        Decimal result6 = new Decimal();
        DecimalParser.parseNumberToDelim("1", position.set(0), ',',result6);
        Assertions.assertEquals(new Decimal().setLong(1).setScale(0),result6);

        Decimal result7 = new Decimal();
        DecimalParser.parseNumberToDelim(",1", position.set(0), ',',result7);
        Assertions.assertEquals(new Decimal().setLong(0).setScale(0),result7);
    }
}
