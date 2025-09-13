import java.util.concurrent.CompletableFuture;

public class Test4 {

    public static void main(String[] args) {
        CompletableFuture.supplyAsync(() -> {
                    if (Math.random() > 0.5) {
                        throw new RuntimeException("网络错误!");
                    }
                    return 100;
                })
                .handle((result, ex) -> { // 处理正常结果和异常
                    if (ex != null) {
                        System.out.println("操作失败，错误原因: " + ex.getMessage());
                        return 0; // 异常时返回降级值
                    } else {
                        System.out.println("操作成功！");
                        return result * 0.9; // 成功时转换结果
                    }
                })
                .thenAccept(finalPrice -> System.out.println("最终结果: $" + finalPrice));
    }
}
