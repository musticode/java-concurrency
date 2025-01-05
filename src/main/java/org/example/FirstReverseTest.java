package org.example;

import java.util.ArrayList;
import java.util.List;

public class FirstReverseTest {
    public static void main(String[] args) {
        String coderByte = "coderbyte";
        System.out.println(FirstReverse(coderByte));

        System.out.println("Longest word");
        System.out.println(LongestWord("I love doges"));
        System.out.println(LongestWord("fun&!! time"));
    }

    public static String FirstReverse(String str) {
        StringBuilder result = new StringBuilder(str);
        result.reverse();
        return result.toString();
    }

    public static String LongestWord(String sen) {

        // https://coderbyte.com/editor/Longest%20Word:Java
        // code goes here
        final String regExSpecialChars = "<([{\\^-=$!|]})?*+.>";

        String[] arr = sen.split(regExSpecialChars);
        System.out.println(arr.length);
        int maxIn = 0;
        for (int i = 0; i < arr.length; i++ ){
            for (int j = i + 1; j < arr.length -1 ; j++){
                if (arr[i].length() > arr[j].length()){
                    maxIn = i;
                }
            }
        }




//
//        int min = 0;
//        int maxIndex = 0;
//        for (int i = 0; i < arr.length; i++){
////            if (arr[i].length() > arr[i + 1].length()){
////                maxIndex = i;
////            }
//            if (arr[i].length() == 4){ // 1 < 4
//                maxIndex = i;
//            }
//        }

        return arr[maxIn];
    }

}
