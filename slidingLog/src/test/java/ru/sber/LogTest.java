package ru.sber;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;

public class LogTest {
    private final int maxLogSize = 4;
    private final int expirationPeriodMs = 100;
    private final SlidingLog slidingLog = new SlidingLog(maxLogSize, expirationPeriodMs);

    @Test
    public void makeRequests() throws InterruptedException {

        List<Task> tasks = new ArrayList<>();
        List<Thread> threads = new ArrayList<>();
        int nTasks = maxLogSize + 2;
        for (int i = 0; i < nTasks; i++) {
            tasks.add(new Task(slidingLog));
            threads.add(new Thread(tasks.get(i)));
        }

        for (int i = 0; i < maxLogSize; i++) {
            threads.get(i).start();
        }
        for (int i = 0; i < maxLogSize; i++) {
            threads.get(i).join();
        }

        threads.get(maxLogSize).start();
        threads.get(maxLogSize).join();

        Thread.sleep(expirationPeriodMs);

        threads.get(nTasks - 1).start();
        threads.get(nTasks - 1).join();

        for (int i = 0; i < nTasks; i++) {
            Status expectedResponseStatus = null;
            if (i == maxLogSize) {
                expectedResponseStatus = Status.Rejected;
            } else {
                expectedResponseStatus = Status.Accepted;
            }

            assertEquals(expectedResponseStatus, tasks.get(i).getStatus());
        }
    }
}
