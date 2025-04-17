package org.example;

public class LinkedListLeetCode {
    public static void main(String[] args) {
        // list1 = [1,2,4], list2 = [1,3,4]

        ListNode node1 = new ListNode(1);
        ListNode node2 = new ListNode(2);
        ListNode node3 = new ListNode(4);
        node1.next = node2;
        node2.next = node3;

        ListNode node4 = new ListNode(1);
        ListNode node5 = new ListNode(3);
        ListNode node6 = new ListNode(4);
        node4.next = node5;
        node5.next = node6;

        while(node1.next != null){
            System.out.println(node1.val);
        }


        // https://leetcode.com/problems/merge-two-sorted-lists/description/
        //ListNode result = mergeTwoLists(list1, list2);
        //result.toString();

    }

    public static ListNode mergeTwoLists(ListNode list1, ListNode list2){
        ListNode mergedList = new ListNode();
        int list1Size = 0;
        int list2Size = 0;

        while (list1.next != null){
            list1Size++;
        }

        while (list2.next != null){
            list2Size++;
        }


        int mergedListSize = list1Size + list2Size;
        System.out.println(mergedListSize);
        for (int i = 0; i < mergedListSize; i++){
            mergedList.next = list1.next;
        }


        return mergedList;
    }

}


class ListNode {
    int val;
    ListNode next;

    ListNode() {
    }

    ListNode(int val) {
        this.val = val;
    }

    ListNode(int val, ListNode next) {
        this.val = val;
        this.next = next;
    }

    @Override
    public String toString() {
        return "ListNode{" +
                "val=" + val +
                '}';
    }
}