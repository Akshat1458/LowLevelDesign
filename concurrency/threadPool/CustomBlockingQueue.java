package concurrency.threadPool;

import java.util.LinkedList;
import java.util.Queue;

public class CustomBlockingQueue {

    private final Queue<Runnable> queue = new LinkedList<>();

    public synchronized void push(Runnable task){
        queue.add(task);
        notifyAll();
    }

    public synchronized Runnable pop() throws InterruptedException{
        
        while(queue.isEmpty()){
            wait();
        }
        return queue.poll();

    }


    
}
