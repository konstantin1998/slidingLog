package ru.sber;

import org.junit.Test;
import static org.junit.Assert.*;
import java.time.Instant;

/*
Доказательство корректности алгоритма: slidingLog каждый раз, когда у него вызывают метод push
проверяет не превышено ли максимальное количество запросов за определенный временной интервал,
если превышено, то кидает исключение. При этом проверка количества запросов и добавление новых
делаются атомарно.
В тесте несколько потоков вызывают метод push, если при этом какой-то из этих потоков ловит исключение,
то он устанавливает переменную exceptionWasThrown = true,
тест проходит, если ни в одном из потоков не было исключений.
*/

public class SlidingLogTest {
    private boolean exceptionWasThrown = false;

    @Test
    public void mustKeepLogSizeLessThanConfiguredValue() {
        int n = 50;
        long expiryPeriod = 100;
        SlidingLog slidingLog = new SlidingLog(n, expiryPeriod);

        makeRequests(slidingLog);
        assertFalse(exceptionWasThrown);
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
                Instant timestamp = Instant.ofEpochMilli(System.currentTimeMillis());
                Request r = new Request(timestamp);
                try {
                    slidingLog.push(r);
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    exceptionWasThrown = true;
                }
            }
        };
    }
}