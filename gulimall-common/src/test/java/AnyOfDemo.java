import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static java.lang.Thread.sleep;

public class AnyOfDemo {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // 模拟向三个不同的镜像源请求同一个数据
        CompletableFuture<String> mirror1 = CompletableFuture.supplyAsync(() -> {
            try {
                return fetchFromMirror("Mirror1", 2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        CompletableFuture<String> mirror2 = CompletableFuture.supplyAsync(() -> {
            try {
                return fetchFromMirror("Mirror2", 1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }); // 这个最快
        CompletableFuture<String> mirror3 = CompletableFuture.supplyAsync(() -> {
            try {
                return fetchFromMirror("Mirror3", 3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        // 组合它们，等待任意一个完成
        CompletableFuture<Object> anyFuture = CompletableFuture.anyOf(mirror1, mirror2, mirror3);

        // 处理第一个返回的结果
        anyFuture.thenAccept(result -> {
            System.out.println("第一个响应被接收: " + result);
        });

        // 等待并获取结果（结果是Object类型，需要强转）
        String firstResult = (String) anyFuture.get();
        System.out.println("镜像: " + firstResult);
    }

    static String fetchFromMirror(String mirrorName, int delay) throws InterruptedException {
        sleep(delay);
        return "数据来自 " + mirrorName;
    }
}