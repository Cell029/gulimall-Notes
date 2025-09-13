import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RunAsyncDemo {
    public static void main(String[] args) {
        // 执行异步任务但不返回结果
        CompletableFuture<Void> future1 = CompletableFuture.runAsync(() -> {
            System.out.println(Thread.currentThread().getName() + " 执行无返回值的异步任务 2 s");
            simulateLongRunningTask(2);
            System.out.println(Thread.currentThread().getName() + " 执行完毕");
            // 没有 return 语句
        });

        // 记录日志或发送通知等副作用操作
        CompletableFuture<Void> logFuture = CompletableFuture.runAsync(() -> {
            System.out.println(Thread.currentThread().getName() + " [INFO] 开始记录日志");
        });

        // 使用自定义线程池执行资源释放任务
        ExecutorService ioBoundExecutor = Executors.newCachedThreadPool();
        CompletableFuture<Void> fileOperationFuture = CompletableFuture.runAsync(() -> {
            System.out.println("执行 IO 操作: " + Thread.currentThread().getName());
            // 模拟文件I/O操作
            simulateLongRunningTask(1);
        }, ioBoundExecutor);

        // 虽然不需要结果，但可以等待任务完成（例如，在主程序退出前）
        future1.join(); // join() 与 get() 类似，但不抛出受检异常
        fileOperationFuture.join();

        System.out.println("主线程执行完毕");
        ioBoundExecutor.shutdown();
    }

    private static void simulateLongRunningTask(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}