package org.example.states;

public class WaitingState {
    public static void main(String[] args) throws InterruptedException {


        System.out.println("WAITING");
        WaitingState waitingState = new WaitingState();
//        Thread A = new Thread(waitingState::businessProcessing, "A");
//        Thread B = new Thread(waitingState::repairComputer, "B");
//
//        A.start();
//        Thread.sleep(500);
//        B.start();
//        System.out.println("Thread[" + A.getName() + "] state:" + A.getState());
//        System.out.println("Thread[" + B.getName() + "] state:" + B.getState());


        System.out.println("JOIN CASE");
        Thread A = new Thread(waitingState::businessProcessing, "A");
        Thread B = new Thread(waitingState::businessProcessing, "B");
        Thread C = new Thread(waitingState::businessProcessing, "C");

        System.out.println("Please ask thread A to go to the window to handle the business");
        A.start();
        A.join();

        System.out.println("Please ask thread B to go to the window to handle the business");
        B.start();
        B.join();

        System.out.println("Please ask thread C to go to the window to handle the business");
        C.start();



    }
    private synchronized void businessProcessing(){
        try{
            System.out.println("Thread[" + Thread.currentThread().getName() + "] expects to process business, but the computer is broken");
            // Release the monitor(lock)

            wait();
            // business processing

            System.out.println("Thread[" + Thread.currentThread().getName() + "] continues to process business");
            Thread.sleep(2000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private synchronized void repairComputer(){
        System.out.println("Thread[" + Thread.currentThread().getName() + "] comes to repair the computer");
        try {
            // simulated repair
            Thread.sleep(1000L);
            System.out.println("Thread[" + Thread.currentThread().getName() + "] has completed the repair.");
            notify();
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }


}


