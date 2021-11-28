package ru.sber;

public class Task implements Runnable{
    private final SlidingLog slidingLog;
    private Status status;

    public Task(SlidingLog slidingLog) {
        this.slidingLog = slidingLog;
    }

    @Override
    public void run() {
        status = slidingLog.push(new Request()).getStatus();
    }

    public Status getStatus() {
        return status;
    }
}
