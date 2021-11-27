package ru.sber;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TimeStampsManager {
    private List<Instant> timestamps;
    private final long expirePeriod;

    public TimeStampsManager(long expirePeriod) {
        this.expirePeriod = expirePeriod;
        timestamps = new ArrayList<>();
    }

    public void deletedOutdatedTimestamps() {
        long currTime = System.currentTimeMillis();
        timestamps = timestamps
                .stream()
                .filter(timestamp -> currTime - timestamp.toEpochMilli() < expirePeriod)
                .collect(Collectors.toList());
    }

    public int getSize() {
        return timestamps.size();
    }

    public void addEntry(Instant timestamp) {
        timestamps.add(timestamp);
    }
}
