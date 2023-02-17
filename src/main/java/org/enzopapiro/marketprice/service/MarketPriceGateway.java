package org.enzopapiro.marketprice.service;

import org.agrona.BitUtil;
import org.agrona.concurrent.UnsafeBuffer;
import org.agrona.concurrent.ringbuffer.OneToOneRingBuffer;
import org.agrona.concurrent.ringbuffer.RingBuffer;
import org.agrona.concurrent.ringbuffer.RingBufferDescriptor;
import org.enzopapiro.marketprice.config.MarketPriceGatewayConfiguration;
import org.enzopapiro.marketprice.util.subscriber.MarketPriceSubscriber;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class MarketPriceGateway {
    protected MarketPriceGatewayConfiguration config;

    protected ExecutorService worker = Executors.newSingleThreadExecutor();

    protected AtomicBoolean running = new AtomicBoolean(true);
    protected MarketPriceSubscriber subscriber;
    private MarketPriceGatewayConsumer consumer;

    private CountDownLatch startLatch = new CountDownLatch(1);

    public MarketPriceGateway() throws IOException {
        this.config = new MarketPriceGatewayConfiguration();
        this.config.load();
    }

    public RingBuffer constructRingBuffer(final int ringBufferSize) {
        // validate
        if (!BitUtil.isPowerOfTwo(ringBufferSize)) {
            throw new IllegalArgumentException(String.format("Supplied ring buffer size is not a power of 2, supplied value was %d", ringBufferSize));
        }

        final int bufferLength = ringBufferSize + RingBufferDescriptor.TRAILER_LENGTH;
        final UnsafeBuffer internalBuffer = new UnsafeBuffer(ByteBuffer.allocateDirect(bufferLength));

        return new OneToOneRingBuffer(internalBuffer);
    }

    public void start() {

        RingBuffer ringBuffer = constructRingBuffer(this.config.getRingbufferSize());

        subscriber = new MarketPriceGatewaySubscriber(this.config, ringBuffer, '\n');

        consumer = new MarketPriceGatewayConsumer(this.config, ringBuffer);

        worker.execute(() -> {
            while (running.get() == true) {
                consumer.doWork();
            }
        });

        startLatch.countDown();
    }

    public void waitForStart() throws InterruptedException {
        startLatch.await();
    }

    public void stop() {
        running.set(false);
    }

    private static ExecutorService simulationThreads = Executors.newFixedThreadPool(2);

    /**
     * The main method serves as an example of how these classes can work.
     *
     * A hard coded array of strings representing a small array of messages is used to submit work into the service,
     * I vary some attributes using a rudimentary String.format(..) operation however in another context this test would
     * have been developed to externalise the market test data to vary the message grouping, the bid/ask rates and other attributes.
     *
     * Similarly, with the "pretend" requests for rates thread, we could externalise the requests and possibly the frequency.
     *
     * This of course would be done for a purpose, so that we could parameterise input and provide a certain level of automation to benchmarking & verification
     * of our business logic.
     *
     *
     * @param args
     *
     * @throws IOException
     * @throws InterruptedException
     */
    public static void main(String[] args) throws IOException, InterruptedException {

        MarketPriceGateway mpg = new MarketPriceGateway();
        mpg.start();
        mpg.waitForStart();

        simulationThreads.execute(()->{

            // NOTE just for a simple simulation, we should really externalise test data.

            String msgs[] = {
                    "%d, EUR/USD, 1.1000,1.2000,01-06-2020 12:01:01:001\n%d, EUR/JPY, 119.60,119.90,01-06-2020 12:01:02:002\n%d, GBP/USD, 1.2500,1.2560,01-06-2020 12:01:02:002",
                    "%d, EUR/USD, 1.1050,1.2050,01-06-2020 12:01:01:001\n%d, EUR/JPY, 119.50,119.95,01-06-2020 12:01:02:002\n%d, GBP/USD, 1.2510,1.2570,01-06-2020 12:01:02:002",
            };
            int id = 1;
            for(int i=0;i<Integer.MAX_VALUE;i++) {
                mpg.onSimulateMessage(String.format(msgs[i%msgs.length], id++, id++, id++));
            }
        });

        simulationThreads.execute(()->{

            // NOTE should externalise
            String [] pairs = { "EURUSD", "EURJPY", "GBPUSD"};

            int i = 0;
            while(true) {
                mpg.onSimulateRequest(pairs[i++%pairs.length]);
            }
        });
    }


    /**
     * JUST FOR TESTING
     * This exists only to assist in fake a market data message comming into the gateway.
     *
     * @param s
     */
    private void onSimulateMessage(String s) {
        subscriber.onMessage(s);
    }
    private void onSimulateRequest(String s) {
        consumer.onRateRequest(s);
    }
}