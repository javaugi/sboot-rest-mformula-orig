/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.interview.hrank.func.predicate;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class PredicateStreamExample {

    public static void main(String[] args) {
        List<String> names = Arrays.asList("John", "Jane", "Jack", "Doe", "Bob", "Alice");

        // Filter with Predicate
        Predicate<String> startsWithJ = name -> name.startsWith("J");
        Predicate<String> longName = name -> name.length() > 3;

        List<String> filteredNames = names.stream()
                .filter(startsWithJ)
                .collect(Collectors.toList());
        System.out.println("Names starting with J: " + filteredNames);

        // Multiple predicates
        List<String> complexFilter = names.stream()
                .filter(startsWithJ.and(longName))
                .collect(Collectors.toList());
        System.out.println("Long names starting with J: " + complexFilter);

        // Using negate
        List<String> notStartingWithJ = names.stream()
                .filter(startsWithJ.negate())
                .collect(Collectors.toList());
        System.out.println("Names not starting with J: " + notStartingWithJ);

        // Numbers example
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        Predicate<Integer> even = n -> n % 2 == 0;
        Predicate<Integer> greaterThan5 = n -> n > 5;

        List<Integer> result = numbers.stream()
                .filter(even.and(greaterThan5))
                .collect(Collectors.toList());
        System.out.println("Even numbers greater than 5: " + result);
    }
}
