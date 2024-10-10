package org.example.volatilekeyword;

public class VolatileKeywordDemo {
    public static volatile int i = 0;

    public static synchronized void add(){
        i++;
    }

    volatile boolean flag;

    public void setTrue() {
        flag = true;
    }

    public void check(){
        while (!flag){
            System.out.println("False!... Please wait..!");
        }
    }
}
