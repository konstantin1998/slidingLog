package ru.sber;

import java.util.Queue;

public class Service {
    private final Queue<Request> queue;

    public Service(Queue<Request> queue) {
        this.queue = queue;
    }

    public void start() {
        Thread t = new Thread(() -> {
            while (true) {
                while (true) {
                    executeRequest();
                    break;
                }
            }
        });
        t.start();
    }

    private void executeRequest() {
        Request r;
        synchronized (queue) {
            while(queue.isEmpty()) {
                doWait();
            }
            r = queue.poll();
        }
        doExecute(r);
    }

    private void doExecute(Request r) { }

    private void doWait() {
        try {
            queue.wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
