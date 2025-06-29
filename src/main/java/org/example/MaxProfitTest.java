package org.example;

///  https://leetcode.com/problems/best-time-to-buy-and-sell-stock/description/
public class MaxProfitTest {
    public static void main(String[] args) {

        System.out.println(maxProfit(new int[]{9,6,4,3,1}));

        System.out.println(maxProfit(new int[]{7,6,4,3,1}));

        System.out.println(maxProfit(new int[]{7,1,5,3,6,4}));

        System.out.println(maxProfit(new int[]{2,4,1})); // 2'de alıp 4'te satabilir, illa 1 olduğu için 0 dönmemeli
    }

    public static int maxProfit(int[] prices) {

        if (prices.length <= 1 || prices.length >= Math.pow(10, 5)) {
            throw new IllegalArgumentException("Array is empty");
        }

        int maxPrice = 0;
        int minPrice = Integer.MAX_VALUE;
        boolean isBuy = false;
        int buyDayIndex = 0;
        int minDayIndex = 0;
        int maxDayIndex = 0;
        int profit = 0;

        for (int i = 0; i < prices.length; i++){

            if (prices[i] <= 0 || prices[i] >= Math.pow(10, 4)) {
                throw new IllegalArgumentException("Array is empty");
            }


            // find min before finding max
            if(prices[i] < minPrice) {
                minPrice = prices[i];
                minDayIndex = i; // index:1

                for (int j = i + 1; j < prices.length; j++) {
                    if (prices[j] > maxPrice) {
                        maxPrice = prices[j];
                        maxDayIndex = j; // index:
                    }
                }

            }



            if (!isBuy && minDayIndex < maxDayIndex) {
                isBuy = true;
                buyDayIndex = minDayIndex;
            }
            profit = maxPrice - minPrice;


        }

        // her 1 için illa ki 0 dönmek zorunda değil
        if (prices[prices.length - 1] < 2) {
            return 0;
        }

        return profit;
    }
}
