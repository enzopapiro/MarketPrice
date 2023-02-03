package org.enzopapiro.marketprice.util.parsing.text.date;

import org.enzopapiro.marketprice.domain.Decimal;
import org.enzopapiro.marketprice.util.parsing.text.Position;
import org.enzopapiro.marketprice.util.parsing.text.number.DecimalParser;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * A simple date time parse, assumes the format dd-MM-yyyy HH:mm:ss:S as implied in the spec,
 * example: 02-06-2020 12:01:01:001.
 *
 * The class is designed to be used in a single thread and is therefore not thread safe.
 *
 * It makes use of the parsing routines that I have written for decimals to extract the components of the date time.
 *
 * The calendar object is cache to assist with performance.
 */
public class DateTimeParser {
    private final Calendar calendar;
    private final TimeZone timeZone;

    private final Decimal number;

    public DateTimeParser(){
        timeZone = TimeZone.getTimeZone("GMT");
        calendar = Calendar.getInstance();
        number = new Decimal();
    }
    public long parse(CharSequence charSequence, Position position, char delim){

        int delimPos = DecimalParser.parseNumberToDelim(charSequence,position,'-', number);
        int day = (int)number.getValue();

        delimPos = DecimalParser.parseNumberToDelim(charSequence,position.increment(),'-', number.reset());
        int month = (int)number.getValue();

        delimPos = DecimalParser.parseNumberToDelim(charSequence,position.increment(),' ', number.reset());
        int year = (int)number.getValue();

        delimPos = DecimalParser.parseNumberToDelim(charSequence,position.increment(),':', number.reset());
        int hour = (int)number.getValue();

        delimPos = DecimalParser.parseNumberToDelim(charSequence,position.increment(),':', number.reset());
        int min = (int)number.getValue();

        delimPos = DecimalParser.parseNumberToDelim(charSequence,position.increment(),':', number.reset());
        int sec = (int)number.getValue();

        delimPos = DecimalParser.parseNumberToDelim(charSequence,position.increment(),':', number.reset());
        int ms = (int)number.getValue();

        //System.out.println(String.format("%d %d %d %d %d %d %d",day,month,year,hour,min,sec,ms));

        calendar.clear();
        calendar.setTimeZone(timeZone);
        calendar.set(Calendar.DAY_OF_MONTH,day);
        calendar.set(Calendar.MONTH,month-1);
        calendar.set(Calendar.YEAR,year);
        calendar.set(Calendar.HOUR_OF_DAY,hour);
        calendar.set(Calendar.MINUTE,min);
        calendar.set(Calendar.SECOND,sec);
        calendar.set(Calendar.MILLISECOND,ms);

        return calendar.getTimeInMillis();
    }
}
