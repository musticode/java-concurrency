package org.example.priority;

public class PriorityDemo {
    public static void main(String[] args) {
//        Thread a = new Thread();
//        System.out.println("Default priority : " + a.getPriority());
//
//        Thread b = new Thread();
//        b.setPriority(9);
//        System.out.println("Set thread priority : " +  b.getPriority());


        Thread t1 = new Thread(new MyRunnable());
        t1.setPriority(1);

        Thread t2 = new Thread(new MyRunnable());
        t2.setPriority(5);

        Thread t3 = new Thread(new MyRunnable());
        t3.setPriority(10);

        t3.start();
        t2.start();
        t1.start();

    }

    static class MyRunnable implements Runnable{
        @Override
        public void run() {
            System.out.printf("The currently executing thread is %s, priority : %d%n",
                    Thread.currentThread().getName(),
                    Thread.currentThread().getPriority()
            );
        }
    }

}



