package org.example.volatilekeyword;







import java.lang.reflect.Field;

//public class UnsafeDemo {
//    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException, InstantiationException {
//        Field field = Unsafe.class.getDeclaredField("theUnsafe");
//        field.setAccessible(true);
//
//
//        Unsafe unsafe = (Unsafe) field.get(null);
//        Student student = (Student) unsafe.allocateInstance(Student.class);
//        Class studentClass = student.getClass();
//        Field name = studentClass.getField("name");
//        Field age = studentClass.getField("age");
//        Field school = studentClass.getDeclaredField("school");
//
//        unsafe.putInt(student, unsafe.objectFieldOffset(age), 19);
//        unsafe.putObject(student, unsafe.objectFieldOffset(name), "Test");
//        Object schoolFieldBase = unsafe.staticFieldBase(school);
//        System.out.println("staticFieldOffset:" + schoolFieldBase);
//
//        long data = 2000;
//        byte size = 1;
//        long memoryAddress = unsafe.allocateMemory(size);
//        unsafe.putAddress(memoryAddress, data);
//
//        long addrData = unsafe.getAddress(memoryAddress);
//        System.out.println("Addr Data : "+ addrData);
//
//    }
//}

//class Student{
//    public Student() {
//    }
//    private String name;
//    private int age;
//    //private static String school = "Stanford University";
//
//    @Override
//    public String toString() {
//        return super.toString();
//    }
//
////}
