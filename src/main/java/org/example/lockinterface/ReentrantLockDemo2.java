package org.example.lockinterface;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockDemo2  implements Runnable{
    public static ReentrantLock lock = new ReentrantLock();
    public static int count = 0;


    @Override
    public void run() {
        for (int i = 0; i < 10000; i++){
            lock.lock();

            lock.tryLock();
            try {
                lock.tryLock(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            try {
                count++;
            }finally {
                // release lock 1st time
                lock.unlock();

                // release lock 2nd time
                lock.unlock();

                // release lock 3rd time
                lock.unlock();
            }
        }
    }

    public void reentrantLockApiTest(){
        lock.lock();

        try {
            System.out.println("Thread : " + Thread.currentThread().getName() + "\tNumber of calls to lock() "+ lock.getHoldCount());

            System.out.println("Is the current lock resource type a fair lock? "+ lock.isFair());

            System.out.println("Currently there are : " + lock.getQueueLength() + " threads are waiting");

            // Check if the specified thread is waiting to acquire the current lock resource
            System.out.println("Is the current thread waiting to acquire the current lock resource? " + lock.hasQueuedThread(Thread.currentThread()));
            // Check if there are threads waiting to acquire the current lock resource
            System.out.println("Is there a thread waiting to acquire the current lock resource? " + lock.hasQueuedThreads());
            // Check if the current thread holds the current lock resource
            System.out.println("Does the current thread hold the current lock resource? " + lock.isHeldByCurrentThread());
            // Check if the current lock resource is held by a thread
            System.out.println("Is the current lock resource occupied by a thread? " + lock.isLocked());
        } finally {
            // Release lock
            lock.unlock();
        }
    }


    public static void main(String[] args) throws InterruptedException {
        ReentrantLockDemo2 task = new ReentrantLockDemo2();
        Thread t1 = new Thread(task);
        Thread t2 = new Thread(task);
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        System.out.println("Execution result " + count);
        task.reentrantLockApiTest();
    }
}
