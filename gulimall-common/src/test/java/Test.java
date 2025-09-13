import org.apache.ibatis.type.LocalDateTimeTypeHandler;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Test {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        CompletableFuture<Void> voidCompletableFuture = CompletableFuture.supplyAsync(() -> {
                    System.out.println("第一阶段：获取用户ID");
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    return 123;
                })
                .thenApply(userId -> { // 转换：userId -> userObject
                    System.out.println("第二阶段：根据ID(" + userId + ")查询用户详情");
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    return "User_" + userId; // 模拟返回用户对象
                })
                .thenAccept(user -> { // 消费：使用用户对象
                    System.out.println("第三阶段：发送欢迎邮件给 " + user);
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                })
                .thenRun(() -> { // 最终回调：不依赖任何结果
                    System.out.println("第四阶段：所有流程完成，记录日志");
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });
        // 主线程立即继续，不会被上面的任何操作阻塞
        System.out.println("主线程已启动异步流水线，现在继续处理其他请求...");
        // 等待一段时间让异步任务执行
        voidCompletableFuture.get();
    }
}
