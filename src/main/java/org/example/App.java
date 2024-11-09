package org.example;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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

        List<List<String>> listOfList = new ArrayList<>();
        for (List<String> list : listOfList){
            for (String item : list){
                System.out.println(item);
            }
        }

        listOfList
                .stream()
                .flatMap(Collection::stream)
                .forEach(System.out::println);

    }
}
