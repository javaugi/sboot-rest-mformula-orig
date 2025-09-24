/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.interview.hrank;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

/*
Key Features:
    1. HashMap Method: O(n) time complexity, O(n) space complexity - most efficient
    2. Sorting Method: O(n log n) time complexity, O(1) space complexity
    3. Multiple Max Elements: Handles cases where multiple elements have the same maximum frequency
    4. Edge Cases: Handles null and empty arrays
    5. Java 8 Streams: Modern functional programming approach
 */
@Slf4j
public class MaxOccurrenceFinder {

    public static void main(String[] args) {
        int[] numbers = {1, 2, 3, 2, 4, 2, 5, 1, 2, 3, 2};
        MaxOccurrenceFinder m = new MaxOccurrenceFinder();
        log.debug("usingHashMapMostEfficient ...");
        m.usingHashMapMostEfficient(numbers);
        log.debug("usingHashMapWithMultiMax ...");
        m.usingHashMapWithMultiMax(numbers);
        log.debug("usingArraySort ...");
        m.usingArraySort(numbers);
        log.debug("usingStreams ...");
        m.usingStreams(numbers);
        log.debug("usingStreams2 ...");
        m.usingStreams2(numbers);
        log.debug("Done");
    }

    private void usingHashMapMostEfficient(int[] numbers) {
        Map<Integer, Integer> result = findOccurrences(numbers);
        int maxElement = findMaxOccurrence(numbers);

        System.out.println("Occurrences: " + result);
        System.out.println("Element with max occurrences: " + maxElement);
    }

    private void usingHashMapWithMultiMax(int[] numbers) {
        Map<Integer, Integer> occurrences = countOccurrences(numbers);
        List<Integer> maxElements = findMaxOccurrenceElements(numbers);

        System.out.println("All occurrences: " + occurrences);
        System.out.println("Elements with max occurrences: " + maxElements);
        System.out.println("Max occurrence count: "
            + (maxElements.isEmpty() ? 0 : occurrences.get(maxElements.get(0))));
    }

    private void usingArraySort(int[] numbers) {
        System.out.println("usingArraySort {}" + findMaxOccurrenceSorted(numbers));
    }

    private void usingStreams2(int[] numbers) {
        Optional<Map.Entry<Integer, Long>> maxEntry
            = Arrays.stream(numbers)
                .boxed()
                .collect(Collectors.groupingBy(
                    Function.identity(),
                    Collectors.counting()
                ))
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByValue());

        if (maxEntry.isPresent()) {
            System.out.println("Element with max occurrences: " + maxEntry.get().getKey());
            System.out.println("Count: " + maxEntry.get().getValue());
        } else {
            System.out.println("No elements found");
        }
    }

    private void usingStreams(int[] numbers) {
        // Using Java 8 streams to find occurrences
        Map<Integer, Long> occurrences = Arrays.stream(numbers)
            .boxed()
            .collect(Collectors.groupingBy(
                Function.identity(),
                Collectors.counting()
            ));

        // Find element with max occurrences
        Optional<Map.Entry<Integer, Long>> maxEntry = occurrences.entrySet()
            .stream()
            .max(Map.Entry.comparingByValue());

        System.out.println("Occurrences: " + occurrences);

        if (maxEntry.isPresent()) {
            System.out.println("Element with max occurrences: " + maxEntry.get().getKey());
            System.out.println("Count: " + maxEntry.get().getValue());
        } else {
            System.out.println("No elements found");
        }
    }

    public static Map<Integer, Integer> findOccurrences(int[] arr) {
        Map<Integer, Integer> frequencyMap = new HashMap<>();

        for (int num : arr) {
            frequencyMap.put(num, frequencyMap.getOrDefault(num, 0) + 1);
        }

        return frequencyMap;
    }

    public static Map<Integer, Integer> countOccurrences(int[] arr) {
        Map<Integer, Integer> frequencyMap = new HashMap<>();

        for (int num : arr) {
            frequencyMap.put(num, frequencyMap.getOrDefault(num, 0) + 1);
        }

        return frequencyMap;
    }

    public static List<Integer> findMaxOccurrenceElements(int[] arr) {
        if (arr == null || arr.length == 0) {
            return new ArrayList<>();
        }

        Map<Integer, Integer> frequencyMap = countOccurrences(arr);
        int maxCount = Collections.max(frequencyMap.values());
        List<Integer> maxElements = new ArrayList<>();

        for (Map.Entry<Integer, Integer> entry : frequencyMap.entrySet()) {
            if (entry.getValue() == maxCount) {
                maxElements.add(entry.getKey());
            }
        }

        return maxElements;
    }

    public static int findMaxOccurrence(int[] arr) {
        if (arr == null || arr.length == 0) {
            throw new IllegalArgumentException("Array cannot be null or empty");
        }

        Map<Integer, Integer> frequencyMap = new HashMap<>();
        int maxCount = 0;
        int maxElement = arr[0];

        for (int num : arr) {
            int count = frequencyMap.getOrDefault(num, 0) + 1;
            frequencyMap.put(num, count);

            if (count > maxCount) {
                maxCount = count;
                maxElement = num;
            }
        }

        return maxElement;
    }

    public static int findMaxOccurrenceSorted(int[] arr) {
        if (arr == null || arr.length == 0) {
            throw new IllegalArgumentException("Array cannot be null or empty");
        }

        Arrays.sort(arr);

        int maxCount = 1;
        int currentCount = 1;
        int maxElement = arr[0];

        for (int i = 1; i < arr.length; i++) {
            if (arr[i] == arr[i - 1]) {
                currentCount++;
            } else {
                currentCount = 1;
            }

            if (currentCount > maxCount) {
                maxCount = currentCount;
                maxElement = arr[i];
            }
        }

        return maxElement;
    }

}
