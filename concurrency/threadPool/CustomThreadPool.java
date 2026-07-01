package concurrency.threadPool;

public class CustomThreadPool {

    private final Thread[] workers;
    private final CustomBlockingQueue taskQueue;

    public CustomThreadPool(Integer poolSize) {

        workers = new Thread[poolSize];
        taskQueue = new CustomBlockingQueue();

        for (int i = 0; i < poolSize; i++) {
            workers[i] = new Thread(new Worker(taskQueue));
            workers[i].start();
        }
    }

    public void submit(Runnable task) {
        taskQueue.push(task);
    }

    public void shutDown() {
        for (Thread worker : workers) {
            worker.interrupt();
        }
    }

}
