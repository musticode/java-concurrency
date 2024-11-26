package org.example.executorserv;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecutorTest {
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();


        executorService.execute(() -> {
            System.out.println("Sample runnable");
            System.out.println("Thread execute 1 : "  + Thread.currentThread().getName());
        });


        executorService.execute(() -> {
            for (int i = 0; i < 3; i++){
                System.out.println("Printing record : " + i);
            }
            System.out.println("Thread execute 2 : " + Thread.currentThread().getName());
        });


        System.out.println(executorService.isShutdown());
        System.out.println(executorService.isTerminated());

        executorService.shutdown();


        // callable test

        TestCallable<Integer> callable = new TestCallable<>();
        try {
            callable.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }



    }

}

class TestCallable<Integer> implements Callable<java.lang.Integer> {

    @Override
    public java.lang.Integer call() throws Exception {
        System.out.println("1");
        return 1;
    }
}
