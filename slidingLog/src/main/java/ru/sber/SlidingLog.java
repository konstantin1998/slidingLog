package ru.sber;

import java.util.Queue;

public class SlidingLog {
    private final int n;
    private final Queue<Request> queue;

    public SlidingLog(int n, Queue<Request> queue) {
        this.n = n;
        this.queue = queue;
    }

    public Response push(Request request) {
        synchronized (queue) {
            if (queue.size() > n) {
                throw new RuntimeException("maximum queue size exceeded");
            }

            if (queue.size() == n) {
                return new Response(Status.Rejected);
            }

            queue.add(request);
            queue.notify();
        }
        return new Response(Status.ExecutedSuccessfully);
    }
}
