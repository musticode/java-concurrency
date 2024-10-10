package org.example.atomic;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicMarkableReference;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

public class AtomicFieldUpdaterDemo {

    public static class Candidate{
        int id;
        volatile int score;
    }

    static AtomicIntegerFieldUpdater<Candidate> atomicIntegerFieldUpdater =
            AtomicIntegerFieldUpdater.newUpdater(Candidate.class, "score");

    static AtomicReferenceFieldUpdater<Student, String> atRefUpdate =
            AtomicReferenceFieldUpdater.newUpdater(Student.class, String.class, "name");

    public static AtomicInteger allScore = new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException {
        final Candidate stu = new Candidate();
        Thread[] t = new Thread[10000];
        for (int i = 0; i < 10000; i++){
            t[i] = new Thread(){
              public void run(){
                  if (Math.random() > 0.5){
                      atomicIntegerFieldUpdater.incrementAndGet(stu);
                      allScore.incrementAndGet();
                  }
              }
            };
            t[i].start();
        }

        for (int i = 0; i < 10000; i++){
            t[i].join();
        }

        System.out.println("final score=" + stu.score);
        System.out.println("check all score=" + allScore);

        //AtomicReferenceFieldUpdater simple use
        Student student = new Student("Jerry", 17);
        atRefUpdate.compareAndSet(student, student.name, "Jerry-1");
        System.out.println(student);

    }

    static class Student {
        private String name;
        private int age;

        public Student(String name, int age) {
            this.name = name;
            this.age = age;
        }

        @java.lang.Override
        public java.lang.String toString() {
            return "Student{" +
                    "name='" + name + '\'' +
                    ", age=" + age +
                    '}';
        }
    }
}

