package org.example.concurrentcollection;

import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentHashMapDemo {
    public static void main(String[] args) {

        final Map<String, String> hashMap = new HashMap<>();
        final Map<String, String> hashTable = new Hashtable<>();
        final Map<String, String> concurrentHashMap = new ConcurrentHashMap<>();

        concurrentHashMap.put("key", "value");
        String concurrentHashMapObValue = concurrentHashMap.get("key");

        System.out.println(concurrentHashMapObValue);

        ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
        map.put("one", 1);
        map.put("two", 2);

        System.out.println(map.get("one")); // prints 1
        System.out.println(map.size()); // prints 2

        try {
            map.remove("one");
        } catch (ConcurrentModificationException e) {
            System.out.println(e.getMessage());
        }

        try {
            Map<String, String> newStringHashMap = new HashMap<>();
            newStringHashMap.put("Mustafa", "123");
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }

    }


}
