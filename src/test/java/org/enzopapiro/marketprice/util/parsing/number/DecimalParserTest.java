package org.enzopapiro.marketprice.util.parsing.number;

import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.scale.Scales;
import org.enzopapiro.marketprice.domain.Decimal;
import org.enzopapiro.marketprice.util.parsing.text.Position;
import org.enzopapiro.marketprice.util.parsing.text.number.DecimalParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DecimalParserTest {

    @Test
    public void testScaledDecimalParsing() {
        Decimal result1 = new Decimal();
        Position position = new Position();
        int endIdx1 = DecimalParser.parseNumberToDelim(" - 1234.123,", position.set(0), ',',result1);
        Assertions.assertEquals(new Decimal().setLong(-1234123).setScale(3),result1);

        Decimal result2 = new Decimal();
        int endIdx2 = DecimalParser.parseNumberToDelim("1234.123,", position.set(0), ',',result2);
        Assertions.assertEquals(new Decimal().setLong(-1234123).setScale(3),result1);

        Decimal result3 = new Decimal();
        int endIdx3 = DecimalParser.parseNumberToDelim("12 34.123", position.set(0), ',',result3);
        Assertions.assertEquals(new Decimal().setLong(1234123).setScale(3),result3);

//        Decimal result4 = DecimalParser.parseNumberToDelim("1234123", 0, ',',new Decimal());
//        Assertions.assertEquals(new Decimal().setLong(1234123).setScale(0),result4);
//
//        Decimal result5 = DecimalParser.parseNumberToDelim("12 -34.123", 0, ',',new Decimal());
//        Assertions.assertEquals(new Decimal().setLong(1234123).setScale(3),result5);
//
        Decimal result6 = new Decimal();

        int endIdx6 = DecimalParser.parseNumberToDelim("1", position.set(0), ',',result6);
        Assertions.assertEquals(new Decimal().setLong(1).setScale(0),result6);

//        Decimal result7 = new Decimal();
//        int endIdx7 = DecimalParser.parseNumberToDelim(",1", 0, ',',result7);
//        Assertions.assertEquals(new Decimal().setLong(1).setScale(0),result7);
//
//        Decimal result7 = DecimalParser.parseNumberToDelim("AB1CC", 0, ',',new Decimal());
//        Assertions.assertEquals(new Decimal().setLong(1).setScale(0),result7);
    }

    @Test
    public void testDecimal() {
        ScaleMetrics scale3 = Scales.getScaleMetrics(0);
        DecimalArithmetic arith = scale3.getDefaultArithmetic();
        long value = arith.parse("10");
        System.out.println(value);
    }
}
