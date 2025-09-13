import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Test2 {
    public static void main(String[] args) {
        ExecutorService customExecutor = Executors.newFixedThreadPool(2);
        CompletableFuture.supplyAsync(() -> {
                    System.out.println("SupplyAsync in: " + Thread.currentThread().getName());
                    return "data";
                }, customExecutor)
                .thenApply(data -> { // 同步回调：在 supplyAsync 的同一个线程执行
                    System.out.println("thenApply (sync) in: " + Thread.currentThread().getName());
                    return data.toUpperCase();
                })
                .thenApplyAsync(data -> { // 异步回调：提交到线程池，可能换另一个线程执行
                    System.out.println("thenApplyAsync (async) in: " + Thread.currentThread().getName());
                    return data + "!";
                }, customExecutor)
                .thenAcceptAsync(result -> {
                    System.out.println("Final result: " + result + " in: " + Thread.currentThread().getName());
                }, customExecutor);
        // customExecutor.shutdown();
    }
}
