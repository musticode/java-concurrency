package org.example.atomic;

import java.util.concurrent.atomic.AtomicInteger;

public class AtomicIntegerDemo {
    static AtomicInteger atomicInteger = new AtomicInteger();
    public static class AddThread implements Runnable{

        @Override
        public void run() {
            for (int i = 0; i < 10000; i++){
                atomicInteger.incrementAndGet();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Thread[] threads = new Thread[11];

        for (int i = 1; i <= 10; i++){
            threads[i] = new Thread(new AddThread());
        }

        for (int i = 1; i <= 10; i++){
            threads[i].start();
        }

        for (int i = 1; i <= 10; i++){
            threads[i].join();
        }

        System.out.println(atomicInteger);
    }

}
