package org.example.atomic;

import java.util.concurrent.atomic.AtomicIntegerArray;

public class AtomicIntegerArrayDemo {
    static AtomicIntegerArray atomicArr = new AtomicIntegerArray(10);

    public static class IncrementTask implements Runnable{
        @Override
        public void run() {
            for (int i = 0; i < 10000; i++){
                atomicArr.getAndIncrement(i % atomicArr.length());
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Thread[] threads = new Thread[10];

        for (int i = 0; i < 10; i++){
            threads[i] = new Thread(new IncrementTask());
        }

        for (int i = 0; i < 10; i++){
            threads[i].start();
        }

        for (int i = 0; i < 10; i++){
            threads[i].join();
        }

        System.out.println(atomicArr);
    }

}
