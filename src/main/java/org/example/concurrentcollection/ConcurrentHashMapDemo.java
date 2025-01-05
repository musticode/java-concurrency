package org.example.concurrentcollection;

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

    }


}
