package org.example.executorserv;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

public class ExecutorServiceImpl implements Executor {
    public static void main(String[] args) {
        Runnable runnable = new Task();
        System.out.println("Calling task run");
        Thread thread = new Thread(runnable);
        thread.start();
    }


    @Override
    public void execute(Runnable command) {
        new Thread(command).start();
    }

    public void execute(Runnable command, int times){
        System.out.printf("Calling tas run : %d times", times);

        for (int i = 0; i < times; i++){
            execute(command);
        }
    }



}


class Task implements Runnable{

    @Override
    public void run() {
        System.out.println("Calling task run..!");
    }
}

