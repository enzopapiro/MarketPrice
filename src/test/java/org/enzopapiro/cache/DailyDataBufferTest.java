package org.enzopapiro.cache;

import org.agrona.concurrent.UnsafeBuffer;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

public class DailyDataBufferTest {

    @Test
    public void testDailyDataBuffer(){
        BidAskFlyweight bidAsk = new BidAskFlyweight();

        DailyDataBuffer dayBuffer = new DailyDataBuffer(bidAsk.getRecordSize(), 10);

        dayBuffer.wrap(LocalDate.now().toEpochDay(),bidAsk);

        bidAsk.setId(123L);
        bidAsk.setScale(8);
        bidAsk.setBid(111);
        bidAsk.setPupTs(10001);
        bidAsk.setPtxTs(20001);
        bidAsk.setRfqTs(30001);
        bidAsk.setRfqtxTs(30001);


        BidAskFlyweight bidAsk2 = new BidAskFlyweight();

        dayBuffer.wrap(LocalDate.now().toEpochDay(),bidAsk2);

        long bid = bidAsk2.getBid();
        bidAsk2.getRfqtxTs();


    }
}
