/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.interview.hrank.palindrom;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author javau
 */
public class ImprovedPalindromeCounter {

    public static void main(String[] args) {
        String[] arr = {"abc", "def", "aba"};
        System.out.println("#1 Test: " + Arrays.toString(arr) + " -> " + countPossiblePalindromes(arr));
        System.out.println(
                "#2 Test: " + Arrays.toString(arr) + " -> " + countPossiblePalindromesImproved(arr));
    }

    /**
     * Counts how many strings in the array can be turned into palindromes by
     * replacing characters using characters from other strings in the array
     */
    public static int countPossiblePalindromes(String[] arr) {
        if (arr == null || arr.length == 0) {
            return 0;
        }

        int count = 0;

        for (int i = 0; i < arr.length; i++) {
            String currentStr = arr[i];

            // Get all characters from other strings
            String availableChars = getAvailableCharacters(arr, i);

            if (canBecomePalindrome(currentStr, availableChars)) {
                count++;
            }
        }

        return count;
    }

    /**
     * Gets all characters from all strings except the one at excludeIndex
     */
    private static String getAvailableCharacters(String[] arr, int excludeIndex) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            if (i != excludeIndex) {
                sb.append(arr[i]);
            }
        }
        return sb.toString();
    }

    /**
     * Checks if a string can become a palindrome using available replacement
     * characters
     */
    private static boolean canBecomePalindrome(String str, String availableChars) {
        if (str == null || str.isEmpty()) {
            return true;
        }

        // Convert available characters to frequency map for quick lookup
        Map<Character, Integer> availableFreq = getCharacterFrequency(availableChars);

        int left = 0, right = str.length() - 1;

        while (left < right) {
            char leftChar = str.charAt(left);
            char rightChar = str.charAt(right);

            if (leftChar != rightChar) {
                // Characters don't match - we need to replace one or both
                boolean canReplaceLeft = canReplaceCharacter(leftChar, availableFreq, rightChar);
                boolean canReplaceRight = canReplaceCharacter(rightChar, availableFreq, leftChar);

                // We need to be able to make them match by replacement
                if (!canReplaceLeft && !canReplaceRight) {
                    return false;
                }

                // If we use a replacement, consume the character from available pool
                if (canReplaceLeft && leftChar != rightChar) {
                    useCharacterFromPool(rightChar, availableFreq);
                } else if (canReplaceRight) {
                    useCharacterFromPool(leftChar, availableFreq);
                }
            }
            // If characters already match, no replacement needed

            left++;
            right--;
        }

        return true;
    }

    /**
     * Checks if we can replace a character to match the target
     */
    private static boolean canReplaceCharacter(
            char currentChar, Map<Character, Integer> availableFreq, char targetChar) {
        // If we already have the target character available, we can replace
        return availableFreq.getOrDefault(targetChar, 0) > 0;
    }

    /**
     * Uses a character from the available pool
     */
    private static void useCharacterFromPool(char charToUse, Map<Character, Integer> availableFreq) {
        int count = availableFreq.getOrDefault(charToUse, 0);
        if (count > 0) {
            availableFreq.put(charToUse, count - 1);
        }
    }

    /**
     * Alternative approach: Check if character frequencies allow palindrome
     * formation
     */
    private static boolean canBecomePalindromeOptimized(String str, String availableChars) {
        // For a string to be a palindrome, at most one character can have odd frequency
        // We can use available characters to fix the frequency issues

        Map<Character, Integer> strFreq = getCharacterFrequency(str);
        Map<Character, Integer> availableFreq = getCharacterFrequency(availableChars);

        int oddCount = 0;

        // First, try to use the string's own characters to form pairs
        for (Map.Entry<Character, Integer> entry : strFreq.entrySet()) {
            int count = entry.getValue();
            if (count % 2 == 1) {
                oddCount++;
            }
        }

        // If we have more than one character with odd frequency, we need to fix it
        if (oddCount > 1) {
            int fixesNeeded = oddCount - 1;

            // We need to replace some characters to fix the odd counts
            // Each fix requires changing one character to match another
            if (!canFixOddCounts(strFreq, availableFreq, fixesNeeded)) {
                return false;
            }
        }

        return true;
    }

    private static boolean canFixOddCounts(
            Map<Character, Integer> strFreq, Map<Character, Integer> availableFreq, int fixesNeeded) {
        // For each fix needed, we require at least one available character
        // that can be used as replacement
        int availableReplacements = availableFreq.values().stream().mapToInt(Integer::intValue).sum();
        return availableReplacements >= fixesNeeded;
    }

    /**
     * Gets frequency of characters in a string
     */
    private static Map<Character, Integer> getCharacterFrequency(String str) {
        Map<Character, Integer> freq = new HashMap<>();
        for (char c : str.toCharArray()) {
            freq.put(c, freq.getOrDefault(c, 0) + 1);
        }
        return freq;
    }

    /**
     * Improved version of your original approach with better efficiency
     */
    public static int countPossiblePalindromesImproved(String[] arr) {
        if (arr == null || arr.length == 0) {
            return 0;
        }

        int count = 0;

        for (int i = 0; i < arr.length; i++) {
            String currentStr = arr[i];

            if (canBecomePalindromeSimple(currentStr, arr, i)) {
                count++;
            }
        }

        return count;
    }

    /**
     * Simple and efficient check using frequency analysis
     */
    private static boolean canBecomePalindromeSimple(String str, String[] arr, int currentIndex) {
        if (str == null || str.isEmpty()) {
            return true;
        }

        // Get frequency of characters in the current string
        Map<Character, Integer> strFreq = getCharacterFrequency(str);

        // Get all characters from other strings
        Map<Character, Integer> availableChars = new HashMap<>();
        for (int i = 0; i < arr.length; i++) {
            if (i != currentIndex) {
                for (char c : arr[i].toCharArray()) {
                    availableChars.put(c, availableChars.getOrDefault(c, 0) + 1);
                }
            }
        }

        // Check if we can make it a palindrome
        int oddCount = 0;
        for (int count : strFreq.values()) {
            if (count % 2 == 1) {
                oddCount++;
            }
        }

        // For palindrome: at most one character with odd frequency
        if (oddCount <= 1) {
            return true; // Already can be palindrome or needs no replacements
        }

        // We need to reduce oddCount to at most 1
        int replacementsNeeded = oddCount - 1;

        // Check if we have enough available characters to make the replacements
        int totalAvailable = availableChars.values().stream().mapToInt(Integer::intValue).sum();
        return totalAvailable >= replacementsNeeded;
    }
}
