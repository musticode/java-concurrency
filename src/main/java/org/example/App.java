package org.example;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws ExecutionException, InterruptedException {

        List<ZonedDateTime> zonedDateTimes = List.of(
                ZonedDateTime.now(),
                ZonedDateTime.now().minusHours(1),
                ZonedDateTime.now().minusHours(2),
                ZonedDateTime.now().minusHours(3),
                ZonedDateTime.now().minusHours(4)
        );

        System.out.println("PRINT ZONED DATE TIME");

        List<ZonedDateTime> sortedZonedDateTimes = zonedDateTimes.stream().sorted(Comparator.comparing(ZonedDateTime::getDayOfMonth)).toList();

        System.out.println(sortedZonedDateTimes);


        //System.out.println( "Hello World!" );
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> "test").thenApply(res -> res);
        System.out.println(completableFuture.get());

        List<List<String>> listOfList = new ArrayList<>();
        for (List<String> list : listOfList) {
            for (String item : list) {
                System.out.println(item);
            }
        }

        listOfList
                .stream()
                .flatMap(Collection::stream)
                .forEach(System.out::println);

        int[] arr = {1, 2, 3, 4, 5};
        printArray(arr);

    }

    static void printArray(int[] arr) {

        if (arr.length < 0) {
            throw new IllegalArgumentException("Array is empty");
        }

        for (int i = 0; i < arr.length; i++) {
            System.out.println(arr[i]);
        }
    }
}
