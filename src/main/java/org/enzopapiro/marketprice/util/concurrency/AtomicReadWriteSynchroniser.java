package org.enzopapiro.marketprice.util.concurrency;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

/**
 * This class is initialised with an int, write win probability parameter.
 * The class makes of AtomicInteger representing the current thread that hold a
 * read or write lock(lockless/non-blocking).
 *
 * The method exposes tryWrite, tryRead and done to allow a few threads to co-ordinate themselves
 * around a shared resource without blocking and offers a way to avoid thread starvation in a process that
 * requires continually spinning threads to mitigate latency.
 *
 */
public class AtomicReadWriteSynchroniser {
    private final AtomicLong winnerThreadId = new AtomicLong();
    private final int writeWinProbability;

    public AtomicReadWriteSynchroniser(int writeWinProbability) {
        this.writeWinProbability = writeWinProbability;
    }

    public boolean tryWrite() {
        long currentThreadId = Thread.currentThread().getId();
        return winnerThreadId.compareAndSet(0L, currentThreadId);
    }

    public boolean tryRead() {
        long currentThreadId = Thread.currentThread().getId();
        long existingWinnerThreadId = winnerThreadId.get();
        if (existingWinnerThreadId == 0L || existingWinnerThreadId == currentThreadId) {
            return true;
        }
        int randomNumber = ThreadLocalRandom.current().nextInt(100) + 1;
        if (randomNumber > writeWinProbability) {
            // so far reader has won but do a last check to make sure a writer hasn't sneaked in.
            return existingWinnerThreadId == winnerThreadId.get();
        }
        return false;
    }

    public void done() {
        winnerThreadId.set(0L);
    }
}
