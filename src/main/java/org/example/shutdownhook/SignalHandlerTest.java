package org.example.shutdownhook;



//public class SignalHandlerTest implements Terminal.SignalHandler {
//    @Override
//    public void handle(Terminal.Signal signal) {
//        System.out.println("Receive signal is : " + signal.getName() + " - " + signal.getNumber());
//        System.exit(0);
//    }
//
//    public static void main(String[] args) {
//        Runtime.getRuntime().addShutdownHook(new Thread(()-> System.out.println("Shutdown hook is running....!")));
//        SignalHandler sh = new SignalHandlerTest();
//        Signal.handle(new Signal("INT"), sh);
//        Signal.handle(new Signal("TERM"), sh);
//
//        while(true){
//            System.out.println("Main thread is running");
//            try {
//                Thread.sleep(200L);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//
//    }
//}
