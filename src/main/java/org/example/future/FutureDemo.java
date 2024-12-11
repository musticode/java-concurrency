package org.example.future;

import java.sql.SQLOutput;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class FutureDemo {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        final CompletableFuture<Void> future = CompletableFuture.runAsync(new RunnableDemo());
        future.get();



    }



}

class RunnableDemo implements Runnable{

    @Override
    public void run() {
        try {
            System.out.println("Main thread : " + Thread.currentThread().getName());
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }

    }
}
