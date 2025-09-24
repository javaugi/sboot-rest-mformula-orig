/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.interview;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author javau
 */
public class UniqueCombinationFinder {
    public static int[] findMinLengthWithKUnique(int[] nums, int k) {
        int n = nums.length;
        int[] result = new int[n];

        for (int i = 0; i < n; i++) {
            result[i] = findMinLengthFromPosition(nums, i, k);
        }

        return result;
    }

    private static int findMinLengthFromPosition(int[] nums, int start, int k) {
        if (start >= nums.length) {
            return -1;
        }

        Map<Integer, Integer> frequencyMap = new HashMap<>();
        int minLength = Integer.MAX_VALUE;

        for (int end = start; end < nums.length; end++) {
            // Add current element to frequency map
            frequencyMap.put(nums[end], frequencyMap.getOrDefault(nums[end], 0) + 1);

            // Check if we have exactly k unique elements
            if (frequencyMap.size() == k) {
                minLength = Math.min(minLength, end - start + 1);
            } // If we have more than k unique, we can break early (optional optimization)
            else if (frequencyMap.size() > k) {
                break;
            }
        }

        return minLength == Integer.MAX_VALUE ? -1 : minLength;
    }

    // Alternative optimized approach using sliding window
    public static int[] findMinLengthWithKUniqueOptimized(int[] nums, int k) {
        int n = nums.length;
        int[] result = new int[n];

        for (int i = 0; i < n; i++) {
            result[i] = findExactKUniqueFromStart(nums, i, k);
            //result[i] = slidingWindowApproach(nums, i, k);
        }

        return result;
    }

    private static int findExactKUniqueFromStart(int[] nums, int start, int k) {
        if (start >= nums.length) {
            return -1;
        }

        Map<Integer, Integer> frequencyMap = new HashMap<>();
        int minLength = Integer.MAX_VALUE;

        for (int end = start; end < nums.length; end++) {
            // Add current element to frequency map
            frequencyMap.put(nums[end], frequencyMap.getOrDefault(nums[end], 0) + 1);
            // Check if we have exactly k unique elements
            if (frequencyMap.size() == k) {
                minLength = Math.min(minLength, end - start + 1);
            } // If we have more than k unique, we can't get exactly k from this start position
            else if (frequencyMap.size() > k) {
                break;
            }
        }

        return minLength == Integer.MAX_VALUE ? -1 : minLength;
    }

    private static int slidingWindowApproach(int[] nums, int start, int k) {
        if (start >= nums.length) {
            return -1;
        }

        Map<Integer, Integer> freqMap = new HashMap<>();
        int left = start;
        int minLength = Integer.MAX_VALUE;

        for (int right = start; right < nums.length; right++) {
            // Add current element
            freqMap.put(nums[right], freqMap.getOrDefault(nums[right], 0) + 1);
            // Shrink the window from left while we have exactly k unique elements
            while (freqMap.size() == k) {
                minLength = Math.min(minLength, right - left + 1);

                // Remove leftmost element
                freqMap.put(nums[left], freqMap.get(nums[left]) - 1);
                if (freqMap.get(nums[left]) == 0) {
                    freqMap.remove(nums[left]);
                }
                left++;
            }
        }

        return minLength == Integer.MAX_VALUE ? -1 : minLength;
    }

    // Test the solution
    public static void main(String[] args) {
        int[] nums = {1, 5, 3, 5, 7, 5, 2, 9, 2};
        int k = 3;

        System.out.println("Input array: " + Arrays.toString(nums));
        System.out.println("Required unique elements: " + k);

        // Using basic approach
        int[] result1 = findMinLengthWithKUnique(nums, k);
        System.out.println("Basic approach result: " + Arrays.toString(result1));

        // Using optimized sliding window approach
        int[] result2 = findMinLengthWithKUniqueOptimized(nums, k);
        System.out.println("Optimized approach result: " + Arrays.toString(result2));

        // Verify with your expected output
        int[] expected = {3, 4, 3, 4, 3, 3, -1, -1, -1};
        System.out.println("Expected result: " + Arrays.toString(expected));
        System.out.println("result1 Matches expected: " + Arrays.equals(result1, expected));
        System.out.println("result2 Matches expected: " + Arrays.equals(result2, expected));
    }
}
