package org.example;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws ExecutionException, InterruptedException {
        //System.out.println( "Hello World!" );
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(()-> "test").thenApply(res -> res);
        System.out.println(completableFuture.get());
    }
}
