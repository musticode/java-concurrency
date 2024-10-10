package org.example.atomic;



import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicMarkableReference;

public class ABAPProblem {
    static AtomicMarkableReference<Integer> amRef = new AtomicMarkableReference<>(100, false);
    private static Thread thread1 = new Thread(()->{
       boolean mark = amRef.isMarked();
       System.out.println("thread1：mark before modification:" + mark + "....");
        // Update the value to 200
        System.out.println("Thread1  value after modification : " + amRef.compareAndSet(amRef.getReference(), 200, mark, !mark));
    });

    private static Thread thread2 = new Thread(()->{
        boolean mark = amRef.isMarked();
        System.out.println("thread2：mark before modification: " + mark);
        // Update the value to 100
        System.out.println("thread2：200 --> 100.... Return value after modification:" + amRef.compareAndSet(amRef.getReference(), 100, mark, !mark));
    });

    private static Thread thread3 = new Thread(()-> {
       boolean mark = amRef.isMarked();
        System.out.println("Thread3 : mark value before thread sleep : "+ mark );
        try {
            TimeUnit.SECONDS.sleep(1);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        boolean flag = amRef.compareAndSet(100, 500, mark, !mark);
        System.out.println("Thread 3: 100->500... flag: "+ flag + " new value : "+ amRef.getReference());
    });

    public static void main(String[] args) throws InterruptedException {
        thread1.start();
        thread1.join();
        thread2.start();
        thread2.join();
        thread3.start();
    }
}
