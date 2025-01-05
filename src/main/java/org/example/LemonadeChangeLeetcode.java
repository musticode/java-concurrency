package org.example;

public class LemonadeChangeLeetcode {
    public static void main(String[] args) {
        int [] arr = {5,5,5,15,20}; // 5 10 15 - 10 5
        System.out.println(lemonadeChange(arr));

    }

     public static boolean lemonadeChange(int[] bills) {
        int sum = 0;
        for (int i = 0; i < bills.length; i++){
            sum+= bills[i];
            if (bills[i] > 5){
                sum = sum - bills[i]; // 15 -> 15 - 10
                if (bills[i] % 5 != 0){
                    System.out.printf("KALALLNNN");
                    return false;
                }

            }

        }

        return true;
    }

}
