package ru.sber;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.ArrayDeque;
import java.util.Queue;

/*
Доказательство корректности алгоритма: slidingLog каждый раз, когда у него вызывают метод push
проверяет не превышен ли максимальный размер очереди, если превышен, то кидает исключение.
При этом проверка размера очереди происходит атомарно, как и вставка запроса в очередь.
Также сервис атомарно достает из очереди запросы
В тесте несколько потоков вызывают метод push, если при этом какой-то из этих потоков ловит исключение,
то он устанавливает переменную exceptionWasThrown = true,
тест проходит, если ни в одном из потоков не было исключений.
*/

public class SlidingLogTest {
    private boolean exceptionWasThrown = false;

    @Test
    public void test() {
        int n = 1000;
        Queue<Request> queue = new ArrayDeque<>();
        SlidingLog slidingLog = new SlidingLog(n, queue);

        Service service = new Service(queue);
        service.start();
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
                Request r = new Request();
                try {
                    slidingLog.push(r);
                } catch (Exception e) {
                    exceptionWasThrown = true;
                }
            }
        };
    }
}