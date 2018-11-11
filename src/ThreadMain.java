import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadMain {

    public static void main(String args[]) {
        ExecutorService executor = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 10; i++) {
            Thread onlooker = new Onlooker();
            Thread worker = new WorkerBee();
            Thread scout = new Scout();
            executor.execute(onlooker);
            executor.execute(worker);
            executor.execute(scout);
        }
        executor.shutdown();
        while (!executor.isTerminated()) {
        }
        System.out.println("Finished all threads");
    }
}
