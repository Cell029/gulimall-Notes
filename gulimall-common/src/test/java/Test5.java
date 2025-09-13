import java.util.concurrent.CompletableFuture;

public class Test5 {
    public static void main(String[] args) {
        CompletableFuture.supplyAsync(() -> "Result")
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        // 只能记录日志或执行副作用，无法恢复或改变结果
                        System.out.println("任务失败，返回结果: " + result); // 返回 null
                    } else {
                        System.out.println("任务成功: " + result);
                    }
                });
        // whenComplete 返回的 Future 结果与原始 Future 相同（成功则相同，失败则相同异常）
    }
}
