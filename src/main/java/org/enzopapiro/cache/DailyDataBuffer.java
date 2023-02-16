package org.enzopapiro.cache;

import org.agrona.BitUtil;
import org.agrona.BufferUtil;
import org.agrona.concurrent.UnsafeBuffer;

public class DailyDataBuffer {
    private final int recordSize;
    private final int days;
    private final long todayEpochDays;
    private final int totalRecordCount;
    private final UnsafeBuffer buffer;
    public DailyDataBuffer(int recordSize, int years) {
        this.recordSize = recordSize;
        this.days = years * 365;
        this.todayEpochDays = java.time.LocalDate.now().toEpochDay();
        this.totalRecordCount = days + 1; // includes today
        this.buffer = new UnsafeBuffer(BufferUtil.allocateDirectAligned(totalRecordCount * recordSize,BitUtil.findNextPositivePowerOfTwo(recordSize)/*64*/));

    }
    public int getRecordCount() {
        return totalRecordCount;
    }
    public int getRecordOffset(long dateEpochDays) {
        if (dateEpochDays < todayEpochDays - days || dateEpochDays > todayEpochDays) {
            throw new IllegalArgumentException("Date is outside configured range");
        }
        int index = (int)(dateEpochDays - (todayEpochDays - days));
        return index * recordSize;
    }

    public BidAskFlyweight wrap(long dateEpochDays, BidAskFlyweight flyweight) {
        int offset = getRecordOffset(dateEpochDays);
        flyweight.wrap(buffer, offset);
        return flyweight;
    }
}
