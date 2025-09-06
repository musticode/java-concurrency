package org.example;

import java.util.*;

public class StopThreadDemo implements Runnable{
//
//    @Override
//    public void run() {
//        System.out.println("Start moving");
//        for (int i = 1; i <= 5; i++){
//
//
//            try {
//                Thread.sleep(1);
//                System.out.println(i + " batches have been moved");
//            }catch (InterruptedException e){
//                e.printStackTrace();
//            }
//
//
////            if (Thread.currentThread().isInterrupted()){
////                // do sth finishing works
////                System.out.println("closing");
////                break;
////            }
//
////            int j = 50000;
////            while (j > 0){
////                j--;
////            }
////            System.out.println(i + " batches have been moved");
//        }
//        System.out.println("End of moving..!");
//
//    }

    public static void main(String[] args) throws InterruptedException {
//        Thread a = new Thread(new StopThreadDemo());
//        a.start();
//        Thread.sleep(3);
//        a.interrupt();
        int [] expected = {0,1,2,3,4};
        int [] nums = {1,1,2};
        int result = removeDuplicates(nums);
        System.out.println(result);
        int [] nums2 = {0,0,1,1,1,2,2,3,3,4};
        int result2 = removeDuplicates(nums2);
        System.out.println(result2);
    }

    public static int removeDuplicates(int[] nums) {

        int count = 0;

        for (int i = 1; i < nums.length; i++){
            if (nums[count] != nums[i]){
                count++;
                nums[count] = nums[i];
            }
        }


        return count+1;

//        List<Integer> list = new ArrayList<>(nums.length);
//        for (int i = 0; i < nums.length; i++) {
////            if (!list.contains(nums[i])) {
//                list.add(nums[i]);
////            }
//        }
//        Collections.sort(list);
//
//        System.out.println(list);
//
//        int counter = 0;
//        for (int i = 0; i < list.size() -1; i++) {
//            if (i == list.get(i + 1)){
//                counter++;
//            }
//        }
//        return counter;
//        return list.size();

//        Set<Integer> set = new HashSet<>();
//
//        for (int i = 0; i < nums.length; i++) {
//            set.add(nums[i]);
//        }
//
//
//        int setSize = set.size();
//        int numsSize = nums.length;
//        int difference = numsSize - setSize;
//
//
//        for (int i = 0; i < difference; i++) {
//            set.add(0);
//        }
//
//        System.out.println(set);
//
//        return set.size();
    }

    @Override
    public void run() {
        try {
            move();
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
            goBack();
        }
    }

    private void move() throws InterruptedException {
        System.out.println("Start moving...");
        for (int i = 1; i <= 5; i++) {
            //Simulation of time required to move
            Thread.sleep(1);
            System.out.println(i + " batches have been moved");
        }
        System.out.println("End of moving");
    }

    private void goBack() {
        // do some finishing work.
    }

}
