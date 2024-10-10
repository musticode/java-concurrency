package org.example.daemonthread;

public class DaemonThreadDemo {
    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(()-> {
           while (true){
               System.out.println("Daemon thread is running...!");
               try {
                   Thread.sleep(2000L);
               } catch (InterruptedException e) {
                   e.printStackTrace();
               }
           }
        });


        thread.setDaemon(true);
        thread.start();
        Thread.sleep(1000);
        Runtime.getRuntime().addShutdownHook(new Thread(()-> System.out.println("Shutdown hook..!!")));
        System.out.println("The main thread is about to finish execution");
    }


}
