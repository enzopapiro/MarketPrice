package org.enzopapiro.marketprice.util.parsing.delim;

/**
 * This class is responsible for parsing out char subsequences based on a delimiter supplied as a parameter. Once the algorithm
 * discovers a boundary it will make a call on MessageDelimHandler::onMessage passing the complete sequence and the start and end index
 * of the char subsequence.
 *
 * So for the requirements, we would use it to parse out the individual csv messages that are delimited with '\n' however the delimiter could be anything!!
 * Later in the solution we can use it to parse the csv which would obviously use ',' as a delimiter.
 */
public class MessageDelimParser {
    public static void parse(final CharSequence input,final char delim,final MessageDelimHandler delimHandler) {
        int start=0;

        final int length = input.length();

        for(int i = 0; i<length; i++){

            final char c = input.charAt(i);

            if(c == delim){
                // at this point we're at the end of a demarcated section plus the delimiter
                delimHandler.onMessage(input,start,i-1);
                start = i+1;
            } else if(i == length - 1){
                // we're at the end of the block of messages
                delimHandler.onMessage(input,start,i);
            }
        }
    }
}
