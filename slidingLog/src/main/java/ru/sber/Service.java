package ru.sber;

import java.util.Queue;

public class Service {
    private final Queue<Request> queue;
    private volatile boolean isWorking = true;

    public Service(Queue<Request> queue) {
        this.queue = queue;
    }

    public void start() {
        Thread t = new Thread(() -> {
            while (true) {
                while (true) {
                    Request r;
                    synchronized (queue) {
                        while(queue.isEmpty() && isWorking) {
                            doWait();
                        }
                        r = queue.poll();
                    }
                    executeRequest(r);
                    break;
                }
            }
        });
        t.start();
    }

    private void executeRequest(Request r) { }

    private void doWait() {
        try {
            queue.wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        isWorking = false;
    }
}
