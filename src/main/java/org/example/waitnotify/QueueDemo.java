package org.example.waitnotify;

import java.util.LinkedList;
import java.util.Queue;

public class QueueDemo {
    Queue<String> buffer = new LinkedList<String>();

    /**
     * responsible for adding data to buffer
     * and executes the notify method to wake up the thread that was waiting before
     * `take` methot is responsible for checking if the buffer is empty
     * if it is empty, it enters waiting state
     * if it is not empty, it retrieves a data item
     * */
    public void save(String data){
        buffer.add(data);
        notify();
    }

    public String take() throws InterruptedException {
        while (buffer.isEmpty()){
            wait();
        }
        return buffer.remove();
    }

}
