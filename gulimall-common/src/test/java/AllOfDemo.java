import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class AllOfDemo {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // 模拟三个独立的异步任务
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> fetchUserInfo("user123"));
        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> fetchProductInfo("prod456"));
        CompletableFuture<Integer> future3 = CompletableFuture.supplyAsync(() -> checkStock("prod456"));

        // 组合它们，等待所有完成
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(future1, future2, future3);

        // 当所有任务完成后，thenRun() 或 thenAccept() 会被触发
        CompletableFuture<Void> finalFuture = allFutures.thenRun(() -> {
            // 在这里安全地获取每个任务的结果（因为它们肯定完成了），所以调用 get() 不会阻塞
            try {
                String userInfo = future1.get();
                String productInfo = future2.get();
                Integer stock = future3.get();

                System.out.println("整合所有数据:");
                System.out.println(" - " + userInfo);
                System.out.println(" - " + productInfo);
                System.out.println(" - 库存: " + stock);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });

        // 等待最终结果
        finalFuture.get();
    }

    // 模拟异步服务调用
    static String fetchUserInfo(String userId) {
        sleep(1000);
        return "用户信息 " + userId;
    }

    static String fetchProductInfo(String prodId) {
        sleep(1500);
        return "商品信息 " + prodId;
    }

    static Integer checkStock(String prodId) {
        sleep(800);
        return 42;
    }

    static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}