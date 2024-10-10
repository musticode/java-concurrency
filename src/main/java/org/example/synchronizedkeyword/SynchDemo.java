package org.example.synchronizedkeyword;



import static org.example.synchronizedkeyword.SynchBlockDemo.count;

public class SynchDemo {
    public static void main(String[] args) throws InterruptedException {

        SynchBlockDemo synchBlockDemo = new SynchBlockDemo();
        Thread thread1 = new Thread(synchBlockDemo);
        Thread thread2 = new Thread(synchBlockDemo);
        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();
        System.out.println(count);
    }
}

class SynchBlockDemo implements Runnable{
    static int count;

    static void perform(){
        synchronized (SynchBlockDemo.class){
            for (int i = 0; i < 100000; i++) {
                count++;
            }
        }
    }

    @Override
    public void run() {
        perform();
    }


}
