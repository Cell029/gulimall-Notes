import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Test8 {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        CompletableFuture<String> fast = CompletableFuture.supplyAsync(() -> "Fast");
        CompletableFuture<String> slow = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "Slow";
        });

        CompletableFuture<String> result = fast.applyToEither(slow, s -> "Winner: " + s);
        System.out.println(result.get()); // Winner: Fast

        fast.acceptEither(slow, s -> {
            System.out.println("First result: " + s);
        });

    }
}
