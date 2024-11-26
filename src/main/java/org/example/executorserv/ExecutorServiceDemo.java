package org.example.executorserv;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ExecutorServiceDemo {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService executorService = new ThreadPoolExecutor(1, 5 , 0L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>());

        Callable<String> callableTask = () -> {
            System.out.println("Call method called");
            TimeUnit.MILLISECONDS.sleep(2000);
          return "Task execution is done";
        };

        Future<String> future = executorService.submit(callableTask);
        System.out.println(future.get());


        List<Callable<String>> callableTasks = new ArrayList<>();
        callableTasks.add(callableTask);
        callableTasks.add(callableTask);
        callableTasks.add(callableTask);

        String result = executorService.invokeAny(callableTasks);

        List<Future<String>> futures = executorService.invokeAll(callableTasks);

        executorService.shutdown();


//        List<Runnable> notExecutedTasks = executorService.shutdownNow();
//
//        executorService.shutdown();
//        try {
//            if (!executorService.awaitTermination(800, TimeUnit.MILLISECONDS)){
//                executorService.shutdownNow();
//            }
//        } catch (InterruptedException e) {
//            executorService.shutdownNow();
//        }
//


        Future<String> stringFuture = executorService.submit(callableTask);
        System.out.println(stringFuture.get());
        List<Future<String>> stringFutureList = executorService.invokeAll(callableTasks);


    }
}
