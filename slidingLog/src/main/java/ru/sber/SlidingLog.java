package ru.sber;

import java.time.Instant;

public class SlidingLog {
    private final int maxRequestsCount;
    private final TimeStampsManager timeStampsManager;
    private final long expiryPeriod;

    public SlidingLog(int maxRequestsCount, long expiryPeriod) {
        this.maxRequestsCount = maxRequestsCount;
        this.expiryPeriod = expiryPeriod;
        timeStampsManager = new TimeStampsManager(expiryPeriod);
    }

    public Response push(Request request) {
        long timestamp = System.currentTimeMillis();
        synchronized (timeStampsManager) {

            timeStampsManager.deleteOutdatedTimestamps();
            if(timeStampsManager.getSize() < maxRequestsCount) {
                timeStampsManager.addEntry(timestamp);
                return new Response(Status.Accepted);
            }
        }
        return new Response(Status.Rejected);
    }
}
