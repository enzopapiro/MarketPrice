package org.enzopapiro.marketprice.service;

import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.MessageHandler;
import org.agrona.concurrent.ringbuffer.RingBuffer;
import org.enzopapiro.marketprice.config.MarketPriceGatewayConfiguration;
import org.enzopapiro.marketprice.domain.Decimal;
import org.enzopapiro.marketprice.domain.Symbol;
import org.enzopapiro.marketprice.util.parsing.text.Position;
import org.enzopapiro.marketprice.util.parsing.text.date.DateTimeParser;
import org.enzopapiro.marketprice.util.parsing.text.number.DecimalParser;
import org.enzopapiro.marketprice.util.parsing.text.symbol.SymbolParser;

import java.nio.CharBuffer;

public class MarketPriceGatewayConsumer implements MessageHandler {

    public static final char DELIM = ',';
    private final RingBuffer ringBuffer;
    private int processedCount;

    private CharBuffer receiveBuffer;

    private ThreadLocal<SymbolParser> tlSymbolParser = ThreadLocal.withInitial(SymbolParser::new);

    private DateTimeParser dateTimeParser;

    private Position position;

    private Decimal number = new Decimal();
    private Decimal bid = new Decimal();
    private Decimal ask = new Decimal();
    private MarketPriceManager priceManager;
    private ThreadLocal<Position> tlPosition = ThreadLocal.withInitial(Position::new);
    public MarketPriceGatewayConsumer(final MarketPriceGatewayConfiguration config, final RingBuffer ringBuffer){
        this.ringBuffer = ringBuffer;
        this.receiveBuffer = CharBuffer.allocate(256);
        this.dateTimeParser = new DateTimeParser();
        this.position = new Position();
        this.priceManager = new MarketPriceManager(config);
    }

    /**
     * this method will be called on a tight loop, it drives consuming the market data
     * contained within the messages posted onto the ring buffer.
     *
     */
    public void doWork(){
        /* we can save the processed count this can a useful stat */
        processedCount = this.ringBuffer.read(this,1);
    }

    private CharBuffer decodeAsCharSequence(MutableDirectBuffer buffer, int index, int length){
        for (int i = index; i < index + length; i++) {
            receiveBuffer.put((char)buffer.getByte(i));
        }
        receiveBuffer.flip();
        return receiveBuffer;
    }

    private void clearReceiveBuffer(){
        receiveBuffer.clear();
    }


    @Override
    public void onMessage(int msgTypeId, MutableDirectBuffer buffer, int index, int length) {

        try {
            CharSequence msg = decodeAsCharSequence(buffer, index, length);

            //log(msg);

            position.reset();

            DecimalParser.parseNumberToDelim(msg,position, DELIM,number);

            Symbol symbol = tlSymbolParser.get().parse(msg,position.increment(), DELIM);

            DecimalParser.parseNumberToDelim(msg,position.increment(), DELIM,bid);

            DecimalParser.parseNumberToDelim(msg,position.increment(), DELIM,ask);

            long ts = dateTimeParser.parse(msg,position, DELIM);

            priceManager.update(number.getValue(),symbol,bid.getValue(),ask.getValue(),bid.getScale(),ts);

        } catch(Throwable e){
            e.printStackTrace();
        } finally {
            clearReceiveBuffer();
        }
    }

    public void onRateRequest(CharSequence msg){
        Symbol symbol = tlSymbolParser.get().parse(msg,tlPosition.get().reset(), DELIM);
        priceManager.request(symbol);
    }

    private static void log(CharSequence msg) {
        System.out.print("*\t");
        for (int i = 0; i < msg.length(); i++) {
            System.out.print((char) msg.charAt(i));
        }
        System.out.println();
    }
}
