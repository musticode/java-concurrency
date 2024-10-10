package org.example.states;

public class BlockedState {
    public static void main(String[] args) throws InterruptedException {
        BlockedState blockedState = new BlockedState();
        Thread A = new Thread(blockedState::businessProcessing, "A");
        Thread B = new Thread(blockedState::businessProcessing, "B");

        A.start();
        Thread.sleep(1000L);
        /**
         * Thread [A] performs processing
         * Thread[A] state:TIMED_WAITING
         * Thread[B] state:BLOCKED
         * Thread [B] performs processing
         * */
        B.start();

        System.out.println("Thread[" + A.getName() + "] state:" + A.getState());
        System.out.println("Thread[" + B.getName() + "] state:" + B.getState());
    }

    private synchronized void businessProcessing(){
        try {
            System.out.println("Thread [" + Thread.currentThread().getName() + "] performs processing");
            Thread.sleep(2000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
