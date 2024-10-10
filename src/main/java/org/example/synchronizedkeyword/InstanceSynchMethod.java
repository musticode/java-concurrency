package org.example.synchronizedkeyword;

public class InstanceSynchMethod {
    static int count = 0;

//    public static synchronized void increase(){
//        count++;
//    }

    public static void main(String[] args) throws InterruptedException {
        InstanceSynchMethod instanceSynchMethod = new InstanceSynchMethod();
//        Runnable runnable = () -> {
//          for (int i = 0; i < 10000; i++){
//              instanceSynchMethod.increase();
//          }
//        };
        Runnable runnable = () -> {
            synchronized (instanceSynchMethod){
                for (int i = 0; i < 10000; i++){
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
        System.out.println("Count : " + instanceSynchMethod.count);
    }
}
