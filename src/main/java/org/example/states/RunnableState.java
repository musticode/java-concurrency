package org.example.states;

public class RunnableState {
    public static void main(String[] args) {
        Thread thread = new Thread(()-> {});
        System.out.println(thread.getState());

        thread.start(); // first call
        System.out.println(thread.getState());

        thread.start();

    }
}
