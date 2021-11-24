package ru.sber;

import org.junit.Test;

public class SlidingLogTest {
    @Test
    public void test() {
        int n = 1000;
        SlidingLog slidingLog = new SlidingLog(n);
        makeRequests(slidingLog);
        slidingLog.stop();
    }

    private void makeRequests(SlidingLog slidingLog) {
        Thread[] threads = {
                new Thread(getTask(slidingLog)),
                new Thread(getTask(slidingLog)),
                new Thread(getTask(slidingLog)),
                new Thread(getTask(slidingLog))
        };
        for(Thread t: threads) {
            t.start();
        }

        for(Thread t: threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private Runnable getTask(SlidingLog slidingLog) {
        return () -> {
            for(int i = 0; i < 1_000_000; i++) {
                Request r = new Request();
                slidingLog.push(r);
            }
        };
    }
}