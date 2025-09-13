import java.util.concurrent.CompletableFuture;

public class Test3 {
    public static void main(String[] args) {
        CompletableFuture.supplyAsync(() -> {
                    if (Math.random() > 0.5) {
                        throw new RuntimeException("查询数据库失败！");
                    }
                    return "Data from DB";
                })
                .thenApply(data -> data.toUpperCase()) // 如果上一步异常，此步不会执行
                .exceptionally(ex -> { // 捕获任何阶段的异常
                    System.err.println("Exception occurred: " + ex.getMessage());
                    return "Default Data"; // 提供降级结果，类型必须与正常结果一致(String)
                })
                .thenAccept(data -> System.out.println("Processing: " + data)); // 会接收到正常数据或降级数据
    }
}
