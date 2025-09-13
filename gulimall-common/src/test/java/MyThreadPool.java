import java.util.concurrent.*;

public class MyThreadPool {
    public static void main(String[] args) {
        // 1. 手动创建线程池
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                2, // corePoolSize: 常驻核心线程数
                5, // maximumPoolSize: 最大线程数
                60, // keepAliveTime: 临时线程空闲存活时间
                TimeUnit.SECONDS, // unit: 时间单位
                new ArrayBlockingQueue<>(10), // workQueue: 有界队列，容量为10
                Executors.defaultThreadFactory(), // threadFactory: 使用默认工厂
                new ThreadPoolExecutor.AbortPolicy() // handler: 默认拒绝策略，直接抛出异常
        );
        // 2. 提交任务 (20 个任务，测试线程池行为)
        for (int i = 0; i < 20; i++) {
            final int taskId = i;
            try {
                executor.execute(() -> {
                    System.out.println("Task " + taskId + " is running on " + Thread.currentThread().getName());
                    try {
                        Thread.sleep(2000); // 模拟任务耗时
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
            } catch (RejectedExecutionException e) {
                System.err.println("Task " + taskId + " was rejected! (队列和线程池已满)");
            }
        }
        // 3. 关闭线程池
        executor.shutdown(); // 平滑关闭，不再接受新任务，等待已提交任务执行完成
        // executor.shutdownNow(); // 立即尝试停止所有正在执行的任务，并返回等待执行的任务列表
    }
}
