package org.example.atomic;

import java.util.concurrent.atomic.AtomicReference;

public class AtomicReferenceDemo {

    public static AtomicReference<Student> studentAtomicReference = new AtomicReference<>();

    public static void main(String[] args) {
        Student s1 = new Student("Tom", 10);
        studentAtomicReference.set(s1);

        Student s2 = new Student("Jerry", 12);
        studentAtomicReference.set(s2);

        studentAtomicReference.compareAndSet(s1, s2);

        System.out.println(studentAtomicReference.get().toString());
    }

    static class Student{
        private String name;
        private int age;

        public Student(String name, int age){
            this.name = name;
            this.age = age;
        }

        @Override
        public String toString() {
            return "Student{" +
                    "name='" + name + '\'' +
                    ", age=" + age +
                    '}';
        }
    }

}
