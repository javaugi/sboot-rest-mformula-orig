/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.interview.hrank;

import java.util.*;

/*
Key Points of the Solution:
    Sliding Window Technique: We use a window that expands to include elements until we have at least k distinct talents, then we try to 
        shrink it from the left to find the minimal window.
    HashMap for Frequency Tracking: We track the frequency of each talent number to know when we have exactly k distinct talents.
    Time Complexity: O(n) for the optimized solutions, O(n²) for the basic one.
    Space Complexity: O(k) for the HashMap storage.

How it works for your example:
    Starting at index 0: The minimal window with 3 distinct talents is [3,2,4] (length 3)
    Starting at index 1: Need [2,4,4,4,2,1] to get talents 2,4,1 (length 6)
    Starting at index 2: [4,4,4,2,1] gives talents 4,2,1 (length 5)

And so on...

The findMinTeamsBest method is the most efficient and should be used for the HackerRank submission as it handles all edge cases and runs in optimal time.
 */
public class TalentTeamFormation {

    public static int[] findMinTeams(int[] talents, int k) {
        int n = talents.length;
        int[] result = new int[n];

        // We need at least k distinct talents for a valid team
        if (k > n) {
            Arrays.fill(result, -1);
            return result;
        }

        // Use sliding window to find minimal subarray with at least k distinct talents
        for (int i = 0; i < n; i++) {
            int minLength = Integer.MAX_VALUE;
            Map<Integer, Integer> talentCount = new HashMap<>();
            int left = i;
            int distinctCount = 0;

            for (int right = i; right < n; right++) {
                // Add current talent to window
                int talent = talents[right];
                talentCount.put(talent, talentCount.getOrDefault(talent, 0) + 1);

                // If this is the first occurrence, increment distinct count
                if (talentCount.get(talent) == 1) {
                    distinctCount++;
                }

                // Try to shrink window from left while maintaining k distinct talents
                while (distinctCount >= k) {
                    int currentLength = right - left + 1;
                    if (currentLength < minLength) {
                        minLength = currentLength;
                    }

                    // Remove leftmost element
                    int leftTalent = talents[left];
                    talentCount.put(leftTalent, talentCount.get(leftTalent) - 1);
                    if (talentCount.get(leftTalent) == 0) {
                        distinctCount--;
                        talentCount.remove(leftTalent);
                    }
                    left++;
                }
            }

            result[i] = (minLength == Integer.MAX_VALUE) ? -1 : minLength;
        }

        return result;
    }

    // Alternative optimized solution using sliding window more efficiently
    public static int[] findMinTeamsOptimized(int[] talents, int k) {
        int n = talents.length;
        int[] result = new int[n];
        Arrays.fill(result, -1);

        if (k > n) {
            return result;
        }

        Map<Integer, Integer> talentFreq = new HashMap<>();
        int left = 0;
        int distinctCount = 0;

        for (int right = 0; right < n; right++) {
            // Add current talent to window
            int talent = talents[right];
            talentFreq.put(talent, talentFreq.getOrDefault(talent, 0) + 1);
            if (talentFreq.get(talent) == 1) {
                distinctCount++;
            }

            // Shrink window while we have enough distinct talents
            while (distinctCount >= k) {
                int windowLength = right - left + 1;

                // For all starting positions from current left to wherever we can maintain k distinct
                // we update their minimum length
                for (int i = left; i <= right; i++) {
                    if (result[i] == -1 || windowLength < result[i]) {
                        result[i] = windowLength;
                    }
                }

                // Move left pointer to shrink window
                int leftTalent = talents[left];
                talentFreq.put(leftTalent, talentFreq.get(leftTalent) - 1);
                if (talentFreq.get(leftTalent) == 0) {
                    distinctCount--;
                    talentFreq.remove(leftTalent);
                }
                left++;
            }
        }

        return result;
    }

    // Most efficient solution - single pass with proper tracking
    public static int[] findMinTeamsBest(int[] talents, int k) {
        int n = talents.length;
        int[] result = new int[n];
        Arrays.fill(result, -1);

        if (k > n) {
            return result;
        }

        Map<Integer, Integer> freqMap = new HashMap<>();
        int left = 0;
        int distinct = 0;

        for (int right = 0; right < n; right++) {
            // Add right element
            int rightTalent = talents[right];
            freqMap.put(rightTalent, freqMap.getOrDefault(rightTalent, 0) + 1);
            if (freqMap.get(rightTalent) == 1) {
                distinct++;
            }

            // When we have enough distinct talents, try to find minimal windows
            while (distinct >= k) {
                int currentLength = right - left + 1;

                // Update result for current left position
                if (result[left] == -1 || currentLength < result[left]) {
                    result[left] = currentLength;
                }

                // Remove left element
                int leftTalent = talents[left];
                freqMap.put(leftTalent, freqMap.get(leftTalent) - 1);
                if (freqMap.get(leftTalent) == 0) {
                    distinct--;
                    freqMap.remove(leftTalent);
                }
                left++;
            }
        }

        return result;
    }

    public static int[] findMinTeamsBestMy(int[] talents, int k) {
        int n = talents.length;
        int[] result = new int[n];
        Arrays.fill(result, -1);

        if (k > n) {
            return result;
        }

        int[] talentsOrig = talents;

        for (int i = 0; i < n; i++) {
            int[] remainings = Arrays.copyOfRange(talentsOrig, i, n);
            Set<Integer> tracker = new HashSet<>();
            for (int j = 0; j < remainings.length; j++) {
                if (!tracker.contains(remainings[j])) {
                    tracker.add(remainings[j]);
                }
                if (tracker.size() >= k) {
                    result[i] = j + 1;
                    break;
                }
            }
        }

        return result;
    }

    public static void main(String[] args) {
        int[] talents = {3, 2, 4, 4, 4, 2, 1, 6, 7};
        int k = 3;

        System.out.println("Input talents: " + Arrays.toString(talents));
        System.out.println("Required distinct talents: " + k);
        System.out.println();

        int[] result1 = findMinTeams(talents, k);
        System.out.println("Basic solution: " + Arrays.toString(result1));

        int[] result2 = findMinTeamsOptimized(talents, k);
        System.out.println("Optimized solution: " + Arrays.toString(result2));

        int[] result3 = findMinTeamsBest(talents, k);
        System.out.println("Best solution: " + Arrays.toString(result3));

        // Expected: [3, 6, 5, 4, 3, 3, 3, -1, -1]
        System.out.println("  Expected: [3, 6, 5, 4, 3, 3, 3, -1, -1]");
        int[] resultMy = findMinTeamsBestMy(talents, k);
        System.out.println("My solution:" + Arrays.toString(resultMy));

        // Test explanation
        System.out.println("\nExplanation:");
        System.out.println("Index 0: [3,2,4] = 3 students (talents 3,2,4)");
        System.out.println("Index 1: [2,4,4,4,2,1] = 6 students (talents 2,4,1)");
        System.out.println("Index 2: [4,4,4,2,1] = 5 students (talents 4,2,1)");
        System.out.println("Index 3: [4,4,2,1] = 4 students (talents 4,2,1)");
        System.out.println("Index 4: [4,2,1] = 3 students (talents 4,2,1)");
        System.out.println("Index 5: [2,1,6] = 3 students (talents 2,1,6)");
        System.out.println("Index 6: [1,6,7] = 3 students (talents 1,6,7)");
        System.out.println("Index 7: [6,7] = -1 (only 2 distinct talents)");
        System.out.println("Index 8: [7] = -1 (only 1 distinct talent)");
    }
}
