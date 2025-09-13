import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Test7 {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> "Hello");
        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> "World");

        CompletableFuture<String> result =
                future1.thenCombine(future2, (s1, s2) -> s1 + " " + s2);
        System.out.println(result.get()); // Hello World

        future1.thenAcceptBoth(future2, (s1, s2) -> {
            System.out.println(s1 + " & " + s2);
        });

        future1.runAfterBoth(future2, () -> {
            System.out.println("Both done!");
        });


    }
}
