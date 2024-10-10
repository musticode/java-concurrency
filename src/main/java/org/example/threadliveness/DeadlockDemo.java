package org.example.threadliveness;

public class DeadlockDemo {
    static Object lock1 = new Object();
    static Object lock2 = new Object();

    public static void main(String[] args) {

        new Thread(()-> {
            try {
                synchronized (lock1){
                    Thread.sleep(500);
                    synchronized (lock2){
                        System.out.println("Thread 1 successfully started");
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();


        new Thread(()-> {
            try {
                synchronized (lock2){
                    Thread.sleep(500);
                    synchronized (lock1){
                        System.out.println("Thread 1 successfully started");
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();

    }
}
