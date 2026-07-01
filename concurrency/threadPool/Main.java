package concurrency.threadPool;

public class Main {

    public static void main(String[] args){
        
        CustomThreadPool threadPool = new CustomThreadPool(2);

        threadPool.submit(() -> {System.out.println("hello from " + Thread.currentThread().getName());});
        threadPool.submit(() -> {System.out.println("hello from " + Thread.currentThread().getName());});

        threadPool.shutDown();
    }
    
}
