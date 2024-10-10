package org.example.shutdownhook;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ShutdownHookDemo {
    public static void main(String[] args) {
        HookRunnable hookRunnable = new HookRunnable();
        Runtime.getRuntime().addShutdownHook(new Thread(hookRunnable));

        Runtime.getRuntime().addShutdownHook(new Thread(hookRunnable));

        System.out.println("The main thread is going to finish executing");
        // Register Shutdown Hook
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                closeConnection();
            }
        });

    }

    private static class HookRunnable implements Runnable{

        @Override
        public void run() {
            try {
                System.out.println("Hook : " + Thread.currentThread().getName() + " is executing");
                Thread.sleep(2000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Hook " + Thread.currentThread().getName() + " is about to end execution");
        }
    }

    private static Connection conn;
    private static void initConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/school_info?useSSL=true&", "root", "root");
            System.out.println("Database connection successful！");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void closeConnection() {
        try {
            conn.close();
            System.out.println("Database connection closed！");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
