package ru.sber;

import java.time.Instant;

public class Request {
    private Instant timestamp;

    public Request(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
