package ru.sber;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TimeStampsManager {
    private List<Long> timestamps;
    private final long expirationPeriod;

    public TimeStampsManager(long expirationPeriod) {
        this.expirationPeriod = expirationPeriod;
        timestamps = new ArrayList<>();
    }

    public void deleteOutdatedTimestamps() {
        long currTime = System.currentTimeMillis();
        timestamps = timestamps
                .stream()
                .filter(timestamp -> currTime - timestamp < expirationPeriod)
                .collect(Collectors.toList());
    }

    public int getSize() {
        return timestamps.size();
    }

    public void addEntry(long timestamp) {
        timestamps.add(timestamp);
    }
}
