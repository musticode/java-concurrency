package org.example.threadsafety;

public class IncorrectRunningResultDemo {
    static int count;
    public static void main(String[] args) throws InterruptedException {
        Runnable runnable = () -> {
            synchronized (IncorrectRunningResultDemo.class) {
                for (int i = 0; i < 10000; i++) {
                    count++;
                }
            }
        };


        Thread thread1 = new Thread(runnable);
        Thread thread2 = new Thread(runnable);
        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();
        System.out.println(count);


    }
}
