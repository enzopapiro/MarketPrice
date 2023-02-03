package org.enzopapiro.marketprice.util.parsing.delim;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MessageDelimParserTest {

    private static StringBuilder builder = new StringBuilder();
    @Test
    public void testDelimParser(){
        MessageDelimParser parser = new MessageDelimParser();

        parseAndAssert(true, parser, "ABC|123|EFG", '|', Arrays.asList("ABC", "123", "EFG"));

        parseAndAssert(true, parser, "ABC|123|EFG|",'|', Arrays.asList("ABC", "123", "EFG"));

        parseAndAssert(true, parser, "ABC\n123\nEFG",'\n', Arrays.asList("ABC", "123", "EFG"));

        parseAndAssert(true, parser, "ABC\n123\nEFG\n",'\n', Arrays.asList("ABC", "123", "EFG"));

        parseAndAssert(true, parser, "ABC\n", '\n', Arrays.asList("ABC"));

        parseAndAssert(false, parser, "ABCABCABC\n", '\n', Arrays.asList("ABC"));

        parseAndAssert(false, parser, "AAAAAA\n", '\n', Arrays.asList("A"));

        parseAndAssert(true, parser, "A", '\n', Arrays.asList("A"));
    }

    private static void parseAndAssert(boolean assertTrue, MessageDelimParser parser, String input, char delim, List<String> assertList) {

        System.out.println(String.format("Running test with input %s delim [%s %d] expecting assertion to be %B",input, delim, (int)delim,assertTrue));

        List<String> tokens = new ArrayList<String>();

        final MessageDelimHandler messageDelimHandler = (s, start, end) -> {
            for (int i = start; i <= end; i++) {
                builder.append(s.charAt(i));
            }
            tokens.add(builder.toString());
            builder.delete(0, builder.length());
        };

        tokens.clear();
        parser.parse(input, delim, messageDelimHandler);
        if(assertTrue) {
            Assertions.assertTrue(tokens.containsAll(assertList));
        }else{
            Assertions.assertFalse(tokens.containsAll(assertList));
        }
    }
}
