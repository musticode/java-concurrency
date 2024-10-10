package org.example.threadgroup;

public class ThreadGroupDemo {
    public static void main(String[] args) {

        Thread subThread = new Thread(()-> {
            System.out.println("SUB");
            System.out.println("The name of the thread group is : " +
                    Thread.currentThread().getThreadGroup().getName()
            );

            System.out.println("Thread name : " +
                    Thread.currentThread().getName()
            );
        });

        subThread.start();
        System.out.println("MAIN");
        System.out.println("The name of the thread group is : " +
                Thread.currentThread().getThreadGroup().getName()
        );

        System.out.println("Thread name : " +
                Thread.currentThread().getName()
        );


        System.out.println("Thread Group");
        ThreadGroup subThreadGroup1 = new ThreadGroup("subThreadGroup1");
        ThreadGroup subThreadGroup2 = new ThreadGroup("subThreadGroup2");

        System.out.println("Sub 1 parent name " + subThreadGroup1.getParent().getName());
        System.out.println("Sub 2 parent name " + subThreadGroup2.getParent().getName());

    }
}
