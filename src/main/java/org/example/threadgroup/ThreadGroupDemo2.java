package org.example.threadgroup;

public class ThreadGroupDemo2 {
    public static void main(String[] args) throws InterruptedException {

        ThreadGroup threadGroup1 = new ThreadGroup("sub1");
        Thread t1 = new Thread(threadGroup1, "t1 in sub1");
        Thread t2 = new Thread(threadGroup1, "t2 in sub1");
        Thread t3 = new Thread(threadGroup1, "t3 in sub1");

        t1.start();
        Thread.sleep(50);
        t2.start();

        int activeThreadCount = threadGroup1.activeCount();
        System.out.println("Active thread in " + threadGroup1.getName() + " thread group : " + activeThreadCount);

        ThreadGroup threadGroup2 = new ThreadGroup("sub2");
        Thread t4 = new Thread(threadGroup2, "t4 in sub2");

        ThreadGroup currentThreadGroup = Thread.currentThread().getThreadGroup();
        int activeGroupCount = currentThreadGroup.activeGroupCount();
        System.out.println("Active thread groups in " + currentThreadGroup.getName() + " thread group: " + activeGroupCount);

        System.out.println("Prints info about currentThreadGroup to the standard group");
        currentThreadGroup.list();
    }
}
