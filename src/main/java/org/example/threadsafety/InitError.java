package org.example.threadsafety;

import java.util.HashMap;
import java.util.Map;

public class InitError {
    private Map<Long, String> students;

    public InitError() {
        new Thread(() -> {
            students = new HashMap<>();
            students.put(1L, "Tom");
            students.put(2L, "Bom");
            students.put(3L, "Com");
        }).start();
    }

    public Map<Long, String> getStudents() {
        return students;
    }

    public static void main(String[] args) {
        InitError initError = new InitError();
        System.out.println(initError.getStudents().get(1L));
    }


}
