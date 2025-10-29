/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.interview.hrank.func.consumer;
import java.util.*;
import java.util.function.Consumer;

public class ConsumerStreamExample {

    public static void main(String[] args) {
        List<String> names = Arrays.asList("John", "Jane", "Jack", "Doe");

        // Simple forEach with Consumer
        names.forEach(name -> System.out.println(name));

        // Method reference
        names.forEach(System.out::println);

        // Complex Consumer
        Consumer<String> processName = name -> {
            String processed = name.toUpperCase() + " - " + name.length();
            System.out.println(processed);
        };

        names.forEach(processName);

        // Consumer with Map
        Map<String, Integer> ageMap = new HashMap<>();
        ageMap.put("John", 25);
        ageMap.put("Jane", 30);

        Consumer<Map.Entry<String, Integer>> entryConsumer
                = entry -> System.out.println(entry.getKey() + " is " + entry.getValue() + " years old");

        ageMap.entrySet().forEach(entryConsumer);
    }
}
