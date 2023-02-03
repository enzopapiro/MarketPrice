package org.enzopapiro.marketprice.util.parsing.text.string;

/**
 * This class uses bit manipulation to store up to 8 chars inside a
 * long value. This provides us with a convenient hash code for dealing with
 * symbol types that are under 8 chars in length such as a currency pair which is 6 chars.
 *
 * chars are cast to a byte and then left shift into the target long zero padding is applied if
 * char sequence is less than 8 chars in length.
 */

public class String8LongEncoder {

    public static long stringToLong(CharSequence s) {
        long result = 0;
        for (int i = 0; i < 8; i++) {
            result = (result << 8) | (i < s.length() ? s.charAt(i) & 0xff : 0);
        }
        return result;
    }

    public static char[] longToCharArray(long value) {
        char[] result = new char[8];
        for (int i = 7; i >= 0; i--) {
            result[i] = (char) (value & 0xff);
            value >>= 8;
        }
        return result;
    }

    public static String longToString(long value) {
        char[] chars = longToCharArray(value);
        int length = 0;
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == 0) {
                break;
            }
            length++;
        }
        return new String(chars, 0, length);
    }
}
