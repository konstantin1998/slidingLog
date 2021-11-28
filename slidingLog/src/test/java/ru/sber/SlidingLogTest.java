package ru.sber;

import org.junit.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;

public class SlidingLogTest {
    private final int maxLogSize = 4;
    private final int expirationPeriodMs = 100;
    private SlidingLog slidingLog = new SlidingLog(maxLogSize, expirationPeriodMs);
    private final int nTasks = maxLogSize + 2;

    @Test
    public void test(){
        int n = 100;
        for(int i = 0; i < n; i++) {
            List<Task> tasks = makeRequests();
            verifyStatuses(tasks);
            refreshSlidingLog();
        }
    }

    private List<Task> makeRequests() {
        List<Task> tasks = initializeTasks();
        List<Thread> threads = initializeThreads(tasks);

        try {
            fillEmptySlotsInSlidingLog(threads);
            makeSlidingLogRejectRequest(threads);
            Thread.sleep(expirationPeriodMs);
            makeSlidingLogClearOldEntriesAndAcceptRequest(nTasks - 1, threads);
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
        return tasks;
    }

    private void refreshSlidingLog() {
        slidingLog = new SlidingLog(maxLogSize, expirationPeriodMs);
    }

    private void verifyStatuses(List<Task> tasks) {
        for (int j = 0; j < nTasks; j++) {
            Status expectedResponseStatus;
            if (j == maxLogSize) {
                expectedResponseStatus = Status.Rejected;
            } else {
                expectedResponseStatus = Status.Accepted;
            }

            assertEquals(expectedResponseStatus, tasks.get(j).getStatus());
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
