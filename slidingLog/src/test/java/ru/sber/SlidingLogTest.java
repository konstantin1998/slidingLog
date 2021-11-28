package ru.sber;

import org.junit.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;

public class SlidingLogTest {
    private final int maxLogSize = 4;
    private final int expirationPeriodMs = 100;
    private final SlidingLog slidingLog = new SlidingLog(maxLogSize, expirationPeriodMs);
    private int nTasks = maxLogSize + 2;

    @Test
    public void makeRequests() throws InterruptedException {

        List<Task> tasks = initializeTasks();
        List<Thread> threads = initializeThreads(tasks);

        fillEmptySlotsInSlidingLog(threads);

        makeSlidingLogRejectRequest(threads);

        Thread.sleep(expirationPeriodMs);

        makeSlidingLogClearOldEntriesAndAcceptRequest(nTasks - 1, threads);

        verifyStatuses(tasks);
    }

    private void verifyStatuses(List<Task> tasks) {
        for (int i = 0; i < nTasks; i++) {
            Status expectedResponseStatus;
            if (i == maxLogSize) {
                expectedResponseStatus = Status.Rejected;
            } else {
                expectedResponseStatus = Status.Accepted;
            }

            assertEquals(expectedResponseStatus, tasks.get(i).getStatus());
        }
    }

    private void fillEmptySlotsInSlidingLog(List<Thread> threads) throws InterruptedException {
        for (int i = 0; i < maxLogSize; i++) {
            threads.get(i).start();
        }
        for (int i = 0; i < maxLogSize; i++) {
            threads.get(i).join();
        }
    }

    private List<Thread> initializeThreads(List<Task> tasks) {
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < nTasks; i++) {
            threads.add(new Thread(tasks.get(i)));
        }
        return threads;
    }

    private List<Task> initializeTasks() {
        List<Task> tasks = new ArrayList<>();
        for (int i = 0; i < nTasks; i++) {
            tasks.add(new Task(slidingLog));
        }
        return tasks;
    }

    private void makeSlidingLogRejectRequest(List<Thread> threads) throws InterruptedException {
        threads.get(maxLogSize).start();
        threads.get(maxLogSize).join();
    }

    private void makeSlidingLogClearOldEntriesAndAcceptRequest(int n, List<Thread> threads) throws InterruptedException {
        threads.get(n).start();
        threads.get(n).join();
    }
}
