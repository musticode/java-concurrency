package org.example.memorymodel;

public class OrderTest {
    int a = 0;
    boolean flag = false;

    public void write(){
        a = 1;
        flag = true;
    }

    public void read(){
        if (flag){
            int i = a + 1;
        }
    }
}
