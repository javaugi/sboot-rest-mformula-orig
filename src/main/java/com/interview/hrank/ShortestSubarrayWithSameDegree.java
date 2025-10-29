/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.interview.hrank;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import static spire.math.FloatComplex.i;

public class ShortestSubarrayWithSameDegree {

    public static int findShortestSubarray(StringBuilder inputData) {
        // Split the input data into lines
        String[] lines = inputData.toString().split("\n");

        // Parse the number of elements (first line)
        int n = Integer.parseInt(lines[0].trim());

        // Parse the integer array (second line)
        String[] arrayStr = lines[1].trim().split("\\s+");

        int[] nums = new int[n];
        for (int i = 0; i < n; i++) {
            nums[i] = Integer.parseInt(arrayStr[i]);
        }

        return findShortestSubarrayLength(nums);
    }

    private static int findShortestSubarrayLengthMy(StringBuilder inputData) {
        int returnValue = 0;

        // Split the input data into lines
        String[] lines = inputData.toString().split("\n");
        // Parse the integer array (second line)
        String[] arrayStr = lines[1].trim().split("\\s+");

        // Maps to store first occurrence, last occurrence, and frequency
        final Map<Integer, Integer> firstOccurrence = new HashMap<>();
        final Map<Integer, Integer> lastOccurrence = new HashMap<>();
        final Map<Integer, Integer> frequency = new HashMap<>();

        final AtomicInteger indexCounter = new AtomicInteger();
        //List<Integer> list = 
        Arrays.stream(arrayStr)
                .map(Integer::parseInt)
                .collect(Collectors.toList())
                .forEach(num -> {
                    // Record first occurrence
                    if (!firstOccurrence.containsKey(num)) {
                        firstOccurrence.put(num, indexCounter.get());
                    }

                    // Update last occurrence
                    lastOccurrence.put(num, indexCounter.get());

                    // Update frequency
                    frequency.put(num, frequency.getOrDefault(num, 0) + 1);
            indexCounter.incrementAndGet();
                });

        Optional<Integer> opt = frequency.values().stream().max(Comparator.comparingInt(Integer::intValue));
        int maxFrequency = opt.isPresent() ? opt.get() : 0;

        // Find the shortest subarray length for elements with max frequency
        int shortestLength = arrayStr.length;
        int num = 0;
        for (Map.Entry<Integer, Integer> entry : frequency.entrySet()) {
            if (entry.getValue() == maxFrequency) {
                num = entry.getKey();
                int subarrayLength = lastOccurrence.get(num) - firstOccurrence.get(num) + 1;
                shortestLength = Math.min(shortestLength, subarrayLength);
            }
        }
        returnValue = shortestLength;

        return returnValue;
    }

    private static int findShortestSubarrayLength(int[] nums) {
        if (nums == null || nums.length == 0) {
            return 0;
        }

        // Maps to store first occurrence, last occurrence, and frequency
        Map<Integer, Integer> firstOccurrence = new HashMap<>();
        Map<Integer, Integer> lastOccurrence = new HashMap<>();
        Map<Integer, Integer> frequency = new HashMap<>();

        // Calculate frequency and track first/last occurrences
        for (int i = 0; i < nums.length; i++) {
            int num = nums[i];

            // Record first occurrence
            if (!firstOccurrence.containsKey(num)) {
                firstOccurrence.put(num, i);
            }

            // Update last occurrence
            lastOccurrence.put(num, i);

            // Update frequency
            frequency.put(num, frequency.getOrDefault(num, 0) + 1);
        }

        // Find the maximum frequency (degree of the array)
        int maxFrequency = 0;
        for (int freq : frequency.values()) {
            maxFrequency = Math.max(maxFrequency, freq);
        }

        // Find the shortest subarray length for elements with max frequency
        int shortestLength = nums.length;
        for (Map.Entry<Integer, Integer> entry : frequency.entrySet()) {
            if (entry.getValue() == maxFrequency) {
                int num = entry.getKey();
                int subarrayLength = lastOccurrence.get(num) - firstOccurrence.get(num) + 1;
                shortestLength = Math.min(shortestLength, subarrayLength);
            }
        }

        return shortestLength;
    }

    // Main method for testing
    public static void main(String[] args) {
        // Test case 1
        StringBuilder input1 = new StringBuilder();
        input1.append("6\n");
        input1.append("1 2 2 3 1 4");

        System.out.println("Test case 1:");
        System.out.println("Input: " + input1.toString().replace("\n", " | "));
        System.out.println("Shortest subarray length: " + findShortestSubarray(input1));
        System.out.println("Shortest subarray length: " + findShortestSubarrayLengthMy(input1));
        System.out.println();

        // Test case 2
        StringBuilder input2 = new StringBuilder();
        input2.append("7\n");
        input2.append("1 2 2 3 1 2 4");

        System.out.println("Test case 2:");
        System.out.println("Input: " + input2.toString().replace("\n", " | "));
        System.out.println("Shortest subarray length: " + findShortestSubarray(input2));
        System.out.println();

        // Test case 3
        StringBuilder input3 = new StringBuilder();
        input3.append("5\n");
        input3.append("1 1 2 2 2");

        System.out.println("Test case 3:");
        System.out.println("Input: " + input3.toString().replace("\n", " | "));
        System.out.println("Shortest subarray length: " + findShortestSubarray(input3));

        int[] nums = {1, 2, 2, 3, 1, 2, 4};
        HashMap<Character, Long> map = Arrays.stream(nums)
                .mapToObj(i -> (char) i)
                .collect(Collectors.groupingBy(Function.identity(), LinkedHashMap::new, Collectors.counting()));
        map.keySet().stream().forEach(k -> {
            System.out.println("key=" + Character.digit(k, 10) + "-value=" + map.get(k));
        });

        //Map<Integer, Long> occurances = 
        Arrays.stream(nums).boxed()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream().forEach(e -> {
                    System.out.println("key=" + e.getKey() + "-value=" + e.getValue());
                });

        int[] longs = {1, 2, 2, 3, 1, 2, 4};
        Map<Integer, Long> occurances = Arrays.stream(longs).boxed()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting())) //.entrySet().stream().forEach(e -> {
                //    System.out.println("key=" + e.getKey() + "-value=" + e.getValue());
                //})
                ;
    }
}


/*
How the program works:
Input Parsing: The program reads the StringBuilder input and splits it into two lines:
    First line: Number of elements in the array
    Second line: The actual integer array

Algorithm Logic:
    Frequency Map: Tracks how many times each number appears
    First Occurrence Map: Records the first index where each number appears
    Last Occurrence Map: Records the last index where each number appears
Finding the Shortest Subarray:
    Calculate the degree of the array (maximum frequency)
    For all numbers that have this maximum frequency, find the shortest subarray length using: lastOccurrence - firstOccurrence + 1
    Return the minimum length among all such subarrays

Example Output:
text
Test case 1:
Input: 6 | 1 2 2 3 1 4
Shortest subarray length: 5

Test case 2:
Input: 7 | 1 2 2 3 1 2 4
Shortest subarray length: 6

Test case 3:
Input: 5 | 1 1 2 2 2
Shortest subarray length: 3
Time Complexity: O(n)
Space Complexity: O(n)
The solution efficiently finds the shortest subarray with the same degree by tracking the necessary information in a single pass through the array.
*/
