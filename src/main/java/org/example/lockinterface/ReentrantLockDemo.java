package org.example.lockinterface;


import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockDemo implements Runnable {

    public static ReentrantLock lock = new ReentrantLock();
    public static int i = 0;

    public static void main(String[] args) throws InterruptedException {
        ReentrantLockDemo reentrantLockDemo = new ReentrantLockDemo();
        Thread t1 = new Thread(reentrantLockDemo);
        Thread t2 = new Thread(reentrantLockDemo);
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        System.out.println(i);

    }

    @Override
    public void run() {
        for (int j= 0; j<100000; j++){
            lock.lock();
            lock.lock();
            try {
                i++;
            }finally {
                lock.unlock();
                lock.unlock();
            }
        }
    }
}
