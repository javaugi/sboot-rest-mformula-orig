/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.interview;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FirstNonRepeatedCharInAStr {

    public static void main(String[] args) {
        FirstNonRepeatedCharInAStr main = new FirstNonRepeatedCharInAStr();
        String ex = "aaaaa";
        log.info("\n 0. Found:" + firstNonRepeatedChar(ex) + " FROM " + ex);
        ex = "khlfhfghgghhg";
        log.info("\n 1. Found:" + firstNonRepeatedChar(ex) + " FROM " + ex);
        log.info("\n 1-2. Found:" + firstMinRepeatedChars(ex) + " FROM " + ex);
        log.info("\n 1-3. Found:" + firstMaxRepeatedChars(ex) + " FROM " + ex);

        log.info("\n 2. Found:" + findFirstNonRepeatableChar(ex) + " FROM " + ex);

        log.info("\n 3. Found:" + findTotalNonRepeatableChars(ex) + " FROM " + ex);

        log.info("\n 4. print non repeatable char FROM " + ex);
        printNonRepeatableChar(ex);
    }

    private static char findFirstNonRepeatableChar(String s) {
        return s.chars().mapToObj(c -> (char) c)
            .collect(Collectors.groupingBy(Function.identity(), LinkedHashMap::new, Collectors.counting()))
            .entrySet().stream().filter(entry -> entry.getValue() == 1)
            .map(m -> m.getKey())
            .findFirst()
            .orElseThrow();
    }

    private static long findTotalNonRepeatableChars(String s) {
        return s.chars().mapToObj(c -> (char) c)
            .collect(Collectors.groupingBy(Function.identity(), HashMap::new, Collectors.counting()))
            .entrySet().stream().filter(entry -> entry.getValue() == 1)
            .map(m -> m.getKey())
            .count();
    }

    private static void printNonRepeatableChar(String s) {
        s.chars().mapToObj(c -> (char) c)
            .collect(Collectors.groupingBy(Function.identity(), LinkedHashMap::new, Collectors.counting()))
            .entrySet().stream().filter(entry -> entry.getValue() == 1)
            .map(m -> m.getKey())
            .forEach(e -> System.out.print(" " + e));
    }

    // Find the first non-repeated character in a string using Java Streams
    public static char firstNonRepeatedChar(String s) {
        try {
            return s.chars()
                .mapToObj(c -> (char) c).collect(Collectors.groupingBy(Function.identity(), LinkedHashMap::new, Collectors.counting()))
                .entrySet().stream()
                .filter(entry -> entry.getValue() == 1)
                .map(Map.Entry::getKey)
                .findFirst().orElse((char) 0);
        } catch (Exception e) {
            log.error("Error {}", s, e);
        }

        return 0;
    }

    // Find the first non-repeated character in a string using Java Streams
    public static char firstMinRepeatedChars(String s) {
        return s.chars()
            .mapToObj(c -> (char) c)
            .collect(Collectors.groupingBy(Function.identity(), LinkedHashMap::new, Collectors.counting()))
            .entrySet().stream()
            .min(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey).get();
    }

    // Find the first non-repeated character in a string using Java Streams
    public static char firstMaxRepeatedChars(String s) {
        return s.chars()
            .mapToObj(c -> (char) c)
            .collect(Collectors.groupingBy(Function.identity(), LinkedHashMap::new, Collectors.counting()))
            .entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey).get();
    }
}
