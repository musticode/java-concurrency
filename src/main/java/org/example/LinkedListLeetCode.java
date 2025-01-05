package org.example;

public class LinkedListLeetCode {
    public static void main(String[] args) {
        // list1 = [1,2,4], list2 = [1,3,4]
        ListNode list1 = new ListNode(1, new ListNode(2, new ListNode(4)));
        ListNode list2 = new ListNode(1, new ListNode(3, new ListNode(4)));

        // https://leetcode.com/problems/merge-two-sorted-lists/description/
        ListNode result = mergeTwoLists(list1, list2);


    }

    public static ListNode mergeTwoLists(ListNode list1, ListNode list2){

        return new ListNode();
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
}