package org.enzopapiro.marketprice.util.parsing.date;

import org.enzopapiro.marketprice.util.parsing.text.Position;
import org.enzopapiro.marketprice.util.parsing.text.date.DateTimeParser;
import org.junit.jupiter.api.Test;

import java.util.Date;

public class DateTimeParserTest {

    @Test
    public void testDateParser() {
        DateTimeParser dateParser = new DateTimeParser();
        Position pos = new Position();
        long ts = dateParser.parse("01-06-2020 12:01:01:001",pos,' ');
        System.out.println(ts == new Date(ts).getTime());
    }
}
