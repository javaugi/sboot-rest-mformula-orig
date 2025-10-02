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

	public static int countPossiblePalindromesSimple(String[] arr) {
		int count = 0;

		String availableChars;
		for (int i = 0; i < arr.length; i++) {
			String str = arr[i];
			if (isPalindrome(str)) {
				// log.debug("*** FOUND i={} str={} count={}", i, str, count);
				count++;
			}
			else {
				availableChars = getAvailableCharacters(arr, i);
				boolean canBe = canBecomePalindromeSimple(str, availableChars);
				if (canBe) {
					count++;
				}
			}
		}

		return count;
	}

	private static String getAvailableCharacters(String[] arr, int excludeIndex) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < arr.length; i++) {
			if (i != excludeIndex) {
				sb.append(arr[i]);
			}
		}
		return sb.toString();
	}

	private static boolean canBecomePalindromeSimple(String str, String availableChars) {
		// log.debug("Else i={} str={} strRemaining={}", i, str, strRemaining);
		boolean found = false;
		for (int j = 0; j < str.length(); j++) {
			for (char c : availableChars.toCharArray()) {
				// String newStr = str.replace(str[i], c);
				String modStr = getModifiedstring(str, j, c);
				// log.debug("Else Looping str={}, j={}, c={} modStr={}", str, j, c,
				// modStr);
				if (isPalindrome(modStr)) {
					// log.debug("*** FOUND i={} j={} str={} count={}", i, j, modStr,
					// count);
					return true;
				}
			}
		}

		return found;
	}

	private static String getModifiedstring(String str, int index, char c) {
		return str.substring(0, index) + c + str.substring(index + 1);
	}

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

	private static void generatePalindromeHelper(Map<Character, Integer> charCount, char[] current, int left, int right,
			List<String> result) {
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
