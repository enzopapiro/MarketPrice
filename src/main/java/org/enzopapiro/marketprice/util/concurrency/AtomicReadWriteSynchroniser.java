package org.enzopapiro.marketprice.util.concurrency;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class AtomicReadWriteSynchroniser {
    private final AtomicLong winnerThreadId = new AtomicLong();
    private final AtomicInteger numReaders = new AtomicInteger();

    private final int writeWinProbability;

    public AtomicReadWriteSynchroniser(int writeWinProbability) {
        this.writeWinProbability = writeWinProbability;
    }

    public boolean tryWrite() {
        long currentThreadId = Thread.currentThread().getId();
        if (numReaders.get() > 0) {
            return false;
        }
        if (winnerThreadId.get() == currentThreadId) {
            return true;
        }
        if (ThreadLocalRandom.current().nextInt(100) < writeWinProbability) {
            boolean success = winnerThreadId.compareAndSet(0L, currentThreadId);
            if (success) {
                numReaders.set(0);
                return true;
            }
        }
        return false;
    }

    public boolean tryRead() {
        long currentThreadId = Thread.currentThread().getId();
        long existingWinnerThreadId = winnerThreadId.get();
        if (existingWinnerThreadId == 0L || existingWinnerThreadId == currentThreadId) {
            numReaders.incrementAndGet();
            return true;
        }
        return false;
    }

    public void done() {
        if (numReaders.get() > 0) {
            numReaders.decrementAndGet();
        } else {
            winnerThreadId.set(0L);
        }
    }
}


