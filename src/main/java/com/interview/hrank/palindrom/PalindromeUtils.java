/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.interview.hrank.palindrom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author javau
 */
public class PalindromeUtils {

    /**
     * Utility method to check if a string is palindrome
     */
    public static boolean isPalindrome(String s) {
        if (s == null) {
            return false;
        }
        int left = 0, right = s.length() - 1;
        while (left < right) {
            if (s.charAt(left) != s.charAt(right)) {
                return false;
            }
            left++;
            right--;
        }
        return true;
    }

    public static boolean isPalindromeSimple(String s) {
        if (s == null) {
            return false;
        }

        return new StringBuilder(s).reverse().toString().equals(s);
    }

    /**
     * Generate all possible palindromes from character pool (for testing)
     */
    public static List<String> generatePalindromes(Map<Character, Integer> charCount, int length) {
        List<String> result = new ArrayList<>();
        generatePalindromeHelper(charCount, new char[length], 0, length - 1, result);
        return result;
    }

    private static void generatePalindromeHelper(
            Map<Character, Integer> charCount, char[] current, int left, int right, List<String> result) {
        if (left > right) {
            result.add(new String(current));
            return;
        }

        if (left == right) {
            // Middle character - can be any character with odd count
            for (Map.Entry<Character, Integer> entry : charCount.entrySet()) {
                if (entry.getValue() > 0) {
                    current[left] = entry.getKey();
                    result.add(new String(current));
                }
            }
            return;
        }

        // Try all characters for the pair (left and right positions)
        for (Map.Entry<Character, Integer> entry : charCount.entrySet()) {
            char ch = entry.getKey();
            int count = entry.getValue();

            if (count >= 2) {
                current[left] = ch;
                current[right] = ch;

                Map<Character, Integer> newCount = new HashMap<>(charCount);
                newCount.put(ch, count - 2);

                generatePalindromeHelper(newCount, current, left + 1, right - 1, result);
            }
        }
    }
}
