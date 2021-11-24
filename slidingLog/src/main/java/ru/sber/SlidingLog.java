package ru.sber;

import java.util.ArrayDeque;
import java.util.Queue;

public class SlidingLog {
    private final int n;
    private final Queue<Request> queue;
    private final Service service;

    public SlidingLog(int n) {
        this.n = n;
        queue = new ArrayDeque<>();
        service = new Service(queue);
        service.start();
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

    public void stop() {
        service.stop();
    }

}
