package concurrency.threadPool;

public class Worker implements Runnable{

    private final CustomBlockingQueue taskQueue;

    public Worker(CustomBlockingQueue taskQueue){
        this.taskQueue = taskQueue;
    }

    @Override
    public void run(){
        
        while (true) {
            try {
                Runnable task = taskQueue.pop();
                if (task != null) {
                    task.run();
                }
            } catch (InterruptedException e) {
                // If interrupted, break out of the loop
                break;
            } catch (Exception e) {
                System.out.println("Error executing task: " + e.getMessage());
            }
        }
    }


    
}
