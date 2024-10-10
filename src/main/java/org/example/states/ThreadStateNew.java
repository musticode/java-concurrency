package org.example.states;

public class ThreadStateNew {
    public static void main(String[] args) {
      Thread thread = new Thread(()-> {});
        System.out.println(thread.getState());
    }
}
