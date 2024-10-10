package org.example.waitnotify;

import java.util.LinkedList;
import java.util.Queue;

public class QueueDemo2 {
    Queue<String> buffer = new LinkedList<String>();

    public synchronized void save(String data){
        System.out.println("Produce a data");
        buffer.add(data);
        notify();
    }

    public synchronized String take() throws InterruptedException {
        System.out.println("Try to consume a data");
        while (buffer.isEmpty()){
            wait();
        }
        return buffer.remove();
    }

    public static void main(String[] args) throws InterruptedException {
        QueueDemo2 queueDemo2 = new QueueDemo2();

        Thread producerThread = new Thread(()-> {
           queueDemo2.save("Hello world");
        });

        Thread consumerThread = new Thread(()-> {
           try {
               System.out.println(queueDemo2.take());
           } catch (InterruptedException e) {
               throw new RuntimeException(e);
           }
        });

        consumerThread.start();
        producerThread.start();


    }

}
