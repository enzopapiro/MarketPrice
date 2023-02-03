package org.enzopapiro.marketprice.service;

import org.agrona.concurrent.AtomicBuffer;
import org.agrona.concurrent.ringbuffer.RingBuffer;
import org.agrona.concurrent.ringbuffer.RingBufferDescriptor;
import org.enzopapiro.marketprice.config.MarketPriceGatewayConfiguration;
import org.enzopapiro.marketprice.util.parsing.delim.MessageDelimHandler;
import org.enzopapiro.marketprice.util.parsing.delim.MessageDelimParser;

/**
 * Assumption:
 * This is the market data subscriber side endpoint i.e. when the message is received by the transport
 * it delivers the sequence of characters that represents one or more market data message.
 */
public class MarketPriceGatewaySubscriber implements org.enzopapiro.marketprice.util.subscriber.MarketPriceSubscriber, MessageDelimHandler {

    private final char messageDelimiter;
    private final RingBuffer ringBuffer;

    public MarketPriceGatewaySubscriber(MarketPriceGatewayConfiguration config, RingBuffer ringBuffer, char messageDelimiter){
        this.messageDelimiter = messageDelimiter;
        this.ringBuffer = ringBuffer;
    }

    /**
     * This is the main entry point for the source providing market prices, we're assuming that the transport will always
     * call this method on the same thread of execution once connection/session is established.
     *
     * @param msg the message
     */
    public void onMessage(CharSequence msg) {
        MessageDelimParser.parse(msg,messageDelimiter,this);
    }

    /**
     * This is method gets called once the message is parsed out into sub messages. It could be called one or more times
     * for each call the transport makes to onMessage(msg). The msg parameter passed is the original in its entirety includes
     * start and end index parameters representing the boundary points of the sub-message.
     * Each sub is then committed to the ring buffer that acts as a queue to the MarketPriceGatewayConsumer.
     * If there is an error or the ring buffer is overwhelmed with too many messages i.e. it is full, then I have made
     * the decision to throw away the incoming sub-message, a form of rate limiting.
     *
     * @param msg - sub message
     * @param start
     * @param end
     */
    @Override
    public void onMessage(CharSequence msg, int start, int end) {

        AtomicBuffer writeBuffer = ringBuffer.buffer();
        int messageLength = (end - start) + 1;
        int recordLength = messageLength + RingBufferDescriptor.TRAILER_LENGTH;
        int claimIndex = ringBuffer.tryClaim(1,messageLength);
        int bytesEncoded=0;
        int tries = 0;
        if (claimIndex > 0) {
            try {
                bytesEncoded = writeBuffer.putStringWithoutLengthAscii(claimIndex, msg, start, messageLength);
                ringBuffer.commit(claimIndex);
            } catch (Exception ex) {
                 ringBuffer.abort(claimIndex);
             }
        }
     }
}
