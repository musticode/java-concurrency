package org.example;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class CompTest {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> "hee");
        String futur = completableFuture.get();
        System.out.println(futur);

        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> "hello");
        CompletableFuture<String> future1 = future.thenApply(s -> s + " world");

        CompletableFuture<Void> future2 = future1.thenRun(() -> System.out.println("Computation finished"));


        future2.get();


        final Executor executor = Executors.newFixedThreadPool(10);
        CompletableFuture.runAsync(() -> {
            try {
                System.out.println("test");
            }catch (RuntimeException e){
                e.printStackTrace();
            }

        }, executor);

    }

    private Integer summation(int x, int y) {
        return x + y;
    }
}
