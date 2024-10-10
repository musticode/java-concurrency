package org.example;

public class StopThreadDemo implements Runnable{
//
//    @Override
//    public void run() {
//        System.out.println("Start moving");
//        for (int i = 1; i <= 5; i++){
//
//
//            try {
//                Thread.sleep(1);
//                System.out.println(i + " batches have been moved");
//            }catch (InterruptedException e){
//                e.printStackTrace();
//            }
//
//
////            if (Thread.currentThread().isInterrupted()){
////                // do sth finishing works
////                System.out.println("closing");
////                break;
////            }
//
////            int j = 50000;
////            while (j > 0){
////                j--;
////            }
////            System.out.println(i + " batches have been moved");
//        }
//        System.out.println("End of moving..!");
//
//    }

    public static void main(String[] args) throws InterruptedException {
        Thread a = new Thread(new StopThreadDemo());
        a.start();
        Thread.sleep(3);
        a.interrupt();
    }

    @Override
    public void run() {
        try {
            move();
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
            goBack();
        }
    }

    private void move() throws InterruptedException {
        System.out.println("Start moving...");
        for (int i = 1; i <= 5; i++) {
            //Simulation of time required to move
            Thread.sleep(1);
            System.out.println(i + " batches have been moved");
        }
        System.out.println("End of moving");
    }

    private void goBack() {
        // do some finishing work.
    }

}
