package org.example;

import java.util.ArrayList;
import java.util.List;

public class FirstReverseTest {
    public static void main(String[] args) {

        String coderByte = "coderbyte";
        System.out.println(FirstReverse(coderByte));
    }

    public static String FirstReverse(String str) {

        StringBuilder result = new StringBuilder(str);
        result.reverse();
        return result.toString();
    }

}
