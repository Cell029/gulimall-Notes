import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Test6 {
    public static void main(String[] args) throws ExecutionException, InterruptedException {

        // 错误用法: 会产生 CompletableFuture<CompletableFuture<String>>
        CompletableFuture<CompletableFuture<String>> badFuture =
                CompletableFuture.supplyAsync(() -> "123")
                        .thenApply(id -> fetchNameAsync(id)); // 返回 Future<String>


        // 正确用法: 使用 thenCompose 展开嵌套的 CompletableFuture
        CompletableFuture<String> goodFuture =
                CompletableFuture.supplyAsync(() -> "123")
                        .thenCompose(id -> fetchNameAsync(id)); // 返回的是 Future<String>

        System.out.println(badFuture.get());
        System.out.println(goodFuture.get()); // 输出: User_123
    }

    private static CompletableFuture<String> fetchNameAsync(String id) {
        return CompletableFuture.supplyAsync(() -> "User_" + id);
    }
}
