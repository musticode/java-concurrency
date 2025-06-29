package org.example;


import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;


public class ShiftZeroTest {

    public static void main(String[] args) {


            int[] nums2 = {2, 1, 1, 1, 2, 2, 3, 3};

            System.out.println("Majority Element");
            System.out.println(majorityElement(nums2));


            int[] nums = {0, 1, 0, 3, 12};

//            moveZeroes(nums);


    }



    public static int majorityElement(int[] nums) {


        double count = 5 * Math.pow(10, 4);

        if (nums.length <= 1 || nums.length >= count) {
            throw new IllegalArgumentException("Array is empty");
        }

        List<Integer> list = new ArrayList<>();
        Arrays.stream(nums).forEach(list::add);


        Map<Integer, List<Integer>> map = new HashMap<>();

        for (int i = 0; i < nums.length; i++) {
            Integer iteratedNumber = i;
            Integer key = nums[i];
            List<Integer> values = list
                    .stream()
                    .filter(item -> item == nums[iteratedNumber])
                    .collect(toList());

//            System.out.println("Key : " + key);
//            System.out.println("Values : " + values);
            map.put(key, values);
        }

        Integer maxListSize = 0;
        Integer key = 0;
        for (Map.Entry<Integer, List<Integer>> entry : map.entrySet()) {
            if (entry.getValue().size() > maxListSize) {
                maxListSize = entry.getValue().size();
                key = entry.getKey();
                System.out.print("Key : " + key);
            }
        }

        return key;
    }



        public static void moveZeroes ( int[] nums){

            List<Integer> numberListWithoutZero = new ArrayList<>();
            int zeroCount = 0;
            for (int i = 0; i < nums.length; i++) {
                if (nums[i] != 0) {
                    numberListWithoutZero.add(nums[i]);
                } else {
                    zeroCount++;
                }
            }

            Comparator<Integer> comparator = Integer::compareTo;

            List<Integer> sortedList = numberListWithoutZero.stream()
                    .sorted(comparator)
                    .collect(toList());

            for (int i = 0; i < zeroCount; i++) {
                sortedList.add(0);
            }


            System.out.print("[");
            for (int i = 0; i < sortedList.size(); i++) {
                if (i == sortedList.size() - 1) {
                    System.out.print(sortedList.get(i));
                } else {
                    System.out.print(sortedList.get(i) + ",");
                }
            }


            System.out.print("]");


        }

    }




