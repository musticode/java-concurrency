package org.example.volatilekeyword;

public class DoubleCheckSingleton {

    private static volatile Singleton singleton;

    private DoubleCheckSingleton(){
    }

    public static Singleton getInstance(){
        if (singleton == null){
            synchronized (Singleton.class){
                if (singleton == null){
                    singleton = new Singleton();
                }
            }
        }
        return singleton;
    }

}

class Singleton{
    public Singleton(){
    }
}
