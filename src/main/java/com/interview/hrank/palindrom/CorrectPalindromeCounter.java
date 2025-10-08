/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.interview.hrank.palindrom;

import lombok.extern.slf4j.Slf4j;

/**
 * @author javau
 */
@Slf4j
public class CorrectPalindromeCounter {

    public static void main(String[] args) {
        String s = null;
        log.debug("String s " + s + " is a Palindrome " + isPalindrome(s));
    }

    public static boolean isPalindrome(String s) {
        /*
        if (s == null || s.length() < 1) {
            return true;
        }
        // */
        return new StringBuilder("" + s).reverse().toString().equals(s);
    }

	/**
	 * Most straightforward correct solution
	 */
	public static int countPossiblePalindromes(String[] arr) {
		if (arr == null || arr.length == 0) {
			return 0;
		}

		int count = 0;

		for (int i = 0; i < arr.length; i++) {
			if (canBecomePalindrome(arr[i], getAllOtherCharacters(arr, i))) {
				count++;
			}
		}

		return count;
	}

	private static String getAllOtherCharacters(String[] arr, int excludeIndex) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < arr.length; i++) {
			if (i != excludeIndex) {
				sb.append(arr[i]);
			}
		}
		return sb.toString();
	}

	private static boolean canBecomePalindrome(String str, String availableChars) {
		// For a string to be a palindrome, at most one character can have odd frequency
		int[] frequency = new int[26]; // Assuming lowercase English letters

		for (char c : str.toCharArray()) {
			frequency[c - 'a']++;
		}

		int oddCount = 0;
		for (int count : frequency) {
			if (count % 2 == 1) {
				oddCount++;
			}
		}

		// If we already have at most one odd count, it can be a palindrome
		if (oddCount <= 1) {
			return true;
		}

		// We need to make replacements to reduce odd count to 1
		// Each replacement can fix one odd count (by making a pair)
		int replacementsNeeded = oddCount - 1;

		// We have all characters from other strings available for replacements
		return availableChars.length() >= replacementsNeeded;
	}

}
