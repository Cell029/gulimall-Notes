import java.util.concurrent.*;

public class SupplyAsyncDemo {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // 使用默认线程池
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
            // 在新的线程中执行的耗时任务
            System.out.println("Task 1 : " + Thread.currentThread().getName());
            simulateLongRunningTask(2); // 模拟耗时 2 秒的操作
            return "Result from Task 1";
        });

        // 使用 Lambda 表达式引用已有方法
        CompletableFuture<Integer> future2 = CompletableFuture.supplyAsync(SupplyAsyncDemo::calculateSomething);

        // 使用自定义线程池
        // 创建一个固定大小的自定义线程池
        ExecutorService customExecutor = Executors.newFixedThreadPool(3, r -> {
            Thread thread = new Thread(r);
            thread.setName("自定义线程：" + thread.getId());
            return thread;
        });

        CompletableFuture<Double> future3 = CompletableFuture.supplyAsync(() -> {
            System.out.println("Task 3 : " + Thread.currentThread().getName());
            simulateLongRunningTask(1);
            return 42.0;
        }, customExecutor); // 显式指定自定义线程池

        // 主线程继续执行，不会被阻塞
        System.out.println("主线程可以执行其它任务: " + Thread.currentThread().getName());

        // 如果需要获取结果，可以调用 get()（这会阻塞主线程）
        String result1 = future1.get(); // 阻塞，直到 future1 完成
        Integer result2 = future2.get();
        Double result3 = future3.get();

        System.out.println("Result 1: " + result1);
        System.out.println("Result 2: " + result2);
        System.out.println("Result 3: " + result3);

        // 关闭自定义线程池，
        customExecutor.shutdown();
    }

    // 模拟耗时操作
    private static void simulateLongRunningTask(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    private static Integer calculateSomething() {
        System.out.println("线程计算中: " + Thread.currentThread().getName());
        simulateLongRunningTask(3);
        return 100 * 2;
    }
}
