package org.enzopapiro.marketprice.util.parsing.text.number;

import org.enzopapiro.marketprice.domain.Decimal;
import org.enzopapiro.marketprice.util.parsing.text.Position;

public class DecimalParser {

    private DecimalParser(){
    }

    public static int parseNumberToDelim(CharSequence charSequence, Position position, char delim, Decimal number) {
        int length = charSequence.length();
        int scale = 0;
        long result = 0;
        int dotIdx=0;
        boolean isNegative=false;
        int endIdx = length;
        for (int i = position.get(); i < length; i++) {
            char c = charSequence.charAt(i);
            if(c != delim) {
                if(c=='-'){
                    if(result==0){
                        isNegative=true;
                    }
                    continue;
                }
                if (c == '.') {
                    dotIdx=i;
                    continue;
                }
                if(c >= '0' && c <= '9') {
                    int digit = c - '0';
                    result = result * 10 + digit;
                    if(dotIdx>0){
                        scale++;
                    }
                }
            } else {
                endIdx = i;
                break;
            }
        }

        number.setScale(scale).setLong(isNegative?result*-1:result);

        position.set(endIdx);

        return endIdx;
    }

    public static void main(String[] args) {

//        Decimal dec = DecimalParser.parseNumber("1234.123", new Decimal());
//        System.out.println(dec);
//
//        Decimal dec2 = DecimalParser.parseNumberToDelim("5551234.123,,", 3, ',', new Decimal());
//        System.out.println(dec2);

        // System.out.println(i);
    }
}
