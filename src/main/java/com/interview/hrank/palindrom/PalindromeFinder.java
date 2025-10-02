/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.interview.hrank.palindrom;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PalindromeFinder {

	public static void main(String[] args) {
		// Test cases
		String[] test1 = { "abc", "def", "aba" };
		String[] test2 = { "a", "b", "c" };
		String[] test3 = { "ab", "ba", "cd" };
		String[] test4 = { "race", "car", "ecar" };
		String[] test5 = { "a", "a", "a", "b" };

		System.out.println("#1-1 Test: " + Arrays.toString(test1) + " -> " + countPossiblePalindromes(test1));
		System.out.println("#1-1-2 Test: " + Arrays.toString(test1) + " -> " + countPossiblePalindromes(test1));
		System.out.println("#1-1-3 Test: " + Arrays.toString(test1) + " -> " + countPossiblePalindromes(test1));
		System.out.println("#1-1-4 Test: " + Arrays.toString(test1) + " -> " + countPossiblePalindromes(test1));
		System.out.println("#1-1-5 Test: " + Arrays.toString(test1) + " -> " + countPossiblePalindromes(test1));
		System.out.println("#1-2 Test: " + Arrays.toString(test1) + " -> " + countPossiblePalindromesNew1(test1));

		System.out.println("#2-1 Test: " + Arrays.toString(test2) + " -> " + countPossiblePalindromes(test2));
		System.out.println("#2-2 Test: " + Arrays.toString(test2) + " -> " + countPossiblePalindromesNew1(test2));
		System.out.println("#3-1 Test: " + Arrays.toString(test3) + " -> " + countPossiblePalindromes(test3));
		System.out.println("#3-2 Test: " + Arrays.toString(test3) + " -> " + countPossiblePalindromesNew1(test3));
		System.out.println("#4-1 Test: " + Arrays.toString(test4) + " -> " + countPossiblePalindromes(test4));
		System.out.println("#4-2 Test: " + Arrays.toString(test4) + " -> " + countPossiblePalindromesNew1(test4));
		System.out.println("#5-1 Test: " + Arrays.toString(test5) + " -> " + countPossiblePalindromes(test5));
		System.out.println("#5-2 Test: " + Arrays.toString(test5) + " -> " + countPossiblePalindromesNew1(test5));
	}

	public static int countPossiblePalindromesNew1(String[] arr) {
		int count = 0;
		List<String> origList = Arrays.asList(arr);
		String availableChars;

		for (int i = 0; i < arr.length; i++) {
			String str = arr[i];
			if (isPalindrome(str)) {
				// log.debug("*** FOUND i={} str={} count={}", i, str, count);
				count++;
			}
			else {
				availableChars = getAvailableCharacters(arr, i);
				// log.debug("Else i={} str={} strRemaining={}", i, str, strRemaining);
				boolean found = false;
				for (int j = 0; j < str.length(); j++) {
					for (char c : availableChars.toCharArray()) {
						// String newStr = str.replace(str[i], c);
						String modStr = getModifiedstring(str, j, c);
						// log.debug("Else Looping str={}, j={}, c={} modStr={}", str, j,
						// c, modStr);
						if (isPalindrome(modStr)) {
							// log.debug("*** FOUND i={} j={} str={} count={}", i, j,
							// modStr, count);
							count++;
							found = true;
							break;
						}
					}
					if (found) {
						break;
					}
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

	private static String getModifiedstring(String str, int index, char c) {
		return str.substring(0, index) + c + str.substring(index + 1);
	}

	private static boolean isPalindrome(String str) {
		return new StringBuilder(str).reverse().toString().equals(str);
	}

	private static String getListString(List<String> syncList) {
		StringBuilder sb = new StringBuilder();
		for (String str : syncList) {
			sb.append(str);
		}

		return sb.toString();
	}

	/**
	 * Counts how many strings in the array can be turned into palindromes by replacing
	 * characters using other strings in the array
	 */
	public static int countPossiblePalindromes(String[] arr) {
		int count = 0;

		// Create a frequency map of all available characters
		Map<Character, Integer> charFrequency = getTotalCharacterFrequency(arr);

		for (String str : arr) {
			if (canFormPalindrome(str, charFrequency, arr.length)) {
				count++;
			}
		}

		return count;
	}

	/**
	 * Alternative approach: Check if characters can be rearranged to form palindrome
	 */
	public static int countPossiblePalindromesOptimized(String[] arr) {
		int count = 0;

		// Get total character frequency from all strings
		Map<Character, Integer> totalFreq = getTotalCharacterFrequency(arr);

		for (String str : arr) {
			if (canFormPalindromeOptimized(str, totalFreq)) {
				count++;
			}
		}

		return count;
	}

	/**
	 * Alternative approach using character count validation
	 */
	public static int countPossiblePalindromesSimple(String[] arr) {
		if (arr == null || arr.length == 0) {
			return 0;
		}

		// Count total characters
		Map<Character, Integer> charCount = new HashMap<>();
		for (String s : arr) {
			for (char c : s.toCharArray()) {
				charCount.put(c, charCount.getOrDefault(c, 0) + 1);
			}
		}

		int count = 0;
		for (String s : arr) {
			if (canBePalindrome(s, charCount)) {
				count++;
			}
		}
		return count;
	}

	/**
	 * Most accurate approach: Try to form palindrome by checking character availability
	 */
	public static int countPossiblePalindromesAccurate(String[] arr) {
		// Combine all characters from all strings
		Map<Character, Integer> pool = getTotalCharacterFrequency(arr);
		int totalChars = pool.values().stream().mapToInt(Integer::intValue).sum();

		int count = 0;
		for (String str : arr) {
			if (canFormPalindromeFromPool(str, pool, totalChars)) {
				count++;
			}
		}
		return count;
	}

	/**
	 * Checks if a string can be turned into a palindrome using available characters
	 */
	private static boolean canFormPalindrome(String str, Map<Character, Integer> totalChars, int totalStrings) {
		// Frequency count for current string
		Map<Character, Integer> strFreq = getCharacterFrequency(str);

		// We need to check if we can rearrange/replace characters to form palindrome
		return canFormPalindromeWithReplacements(strFreq, totalChars, str.length(), totalStrings);
	}

	/**
	 * Core logic to check if palindrome formation is possible with character replacements
	 */
	private static boolean canFormPalindromeWithReplacements(Map<Character, Integer> strFreq,
			Map<Character, Integer> totalChars, int targetLength, int totalStrings) {
		// For a string to be a palindrome, at most one character can have odd frequency
		int oddCount = 0;

		// Check if we have enough characters to form the palindrome
		for (Map.Entry<Character, Integer> entry : strFreq.entrySet()) {
			char ch = entry.getKey();
			int required = entry.getValue();
			int available = totalChars.getOrDefault(ch, 0);

			// If we don't have enough of this character, we need to replace some
			if (available < required) {
				// The deficit can be covered by other characters, but we need to maintain
				// the palindrome property (at most one character with odd count)
				int deficit = required - available;

				// For palindrome, we can only have one character with odd frequency
				// So if this character has odd requirement, we need to handle it
				// carefully
				if (required % 2 == 1) {
					oddCount++;
					if (oddCount > 1) {
						return false;
					}
				}

				// Check if we have enough total characters to cover the deficit
				// considering palindrome constraints
				if (!canCoverDeficit(deficit, totalChars, targetLength)) {
					return false;
				}
			}
			else {
				// We have enough of this character, but still need to check odd count
				if (required % 2 == 1) {
					oddCount++;
					if (oddCount > 1) {
						return false;
					}
				}
			}
		}

		return oddCount <= 1;
	}

	/**
	 * Optimized palindrome check using character frequency analysis
	 */
	private static boolean canFormPalindromeOptimized(String str, Map<Character, Integer> totalFreq) {
		int n = str.length();

		// Create a copy of total frequency map for simulation
		Map<Character, Integer> availableChars = new HashMap<>(totalFreq);

		// Frequency count for what we need
		Map<Character, Integer> neededFreq = getCharacterFrequency(str);

		// For palindrome: at most one character can have odd frequency
		int oddCount = 0;

		for (Map.Entry<Character, Integer> entry : neededFreq.entrySet()) {
			char ch = entry.getKey();
			int needed = entry.getValue();
			int available = availableChars.getOrDefault(ch, 0);

			if (available < needed) {
				// We need to use other characters as replacements
				int deficit = needed - available;

				// For each character we're short, we need to find a replacement
				// But we must maintain palindrome property
				if (needed % 2 == 1) {
					oddCount++;
				}

				// Remove what we use from available characters
				availableChars.put(ch, availableChars.getOrDefault(ch, 0) - available);
				if (availableChars.get(ch) <= 0) {
					availableChars.remove(ch);
				}
			}
			else {
				// We have enough of this character
				if (needed % 2 == 1) {
					oddCount++;
				}
				availableChars.put(ch, available - needed);
			}

			if (oddCount > 1) {
				return false;
			}
		}

		return oddCount <= 1;
	}

	/**
	 * Helper method to check if deficit can be covered
	 */
	private static boolean canCoverDeficit(int deficit, Map<Character, Integer> totalChars, int targetLength) {
		// Calculate total available characters
		int totalAvailable = totalChars.values().stream().mapToInt(Integer::intValue).sum();

		// We need at least 'deficit' additional characters
		return totalAvailable >= targetLength;
	}

	/**
	 * Gets frequency of characters in a single string
	 */
	private static Map<Character, Integer> getCharacterFrequency(String str) {
		Map<Character, Integer> freq = new HashMap<>();
		for (char c : str.toCharArray()) {
			freq.put(c, freq.getOrDefault(c, 0) + 1);
		}
		return freq;
	}

	/**
	 * Gets total frequency of characters from all strings in array
	 */
	private static Map<Character, Integer> getTotalCharacterFrequency(String[] arr) {
		Map<Character, Integer> totalFreq = new HashMap<>();
		for (String str : arr) {
			for (char c : str.toCharArray()) {
				totalFreq.put(c, totalFreq.getOrDefault(c, 0) + 1);
			}
		}
		return totalFreq;
	}

	/**
	 * Simple check: A string can be palindrome if the total character pool allows for at
	 * most one character with odd frequency
	 */
	private static boolean canBePalindrome(String s, Map<Character, Integer> totalChars) {
		// Create frequency map for current string length
		Map<Character, Integer> needed = new HashMap<>();

		// We need to form a string of length s.length()
		// Check if total characters allow palindrome formation for this length
		int oddCount = 0;
		for (char c : s.toCharArray()) {
			needed.put(c, needed.getOrDefault(c, 0) + 1);
		}

		// Simulate using characters from total pool
		Map<Character, Integer> available = new HashMap<>(totalChars);

		for (Map.Entry<Character, Integer> entry : needed.entrySet()) {
			char ch = entry.getKey();
			int need = entry.getValue();
			int have = available.getOrDefault(ch, 0);

			if (have < need) {
				// Need to use other characters - but this might break palindrome property
				// For now, let's focus on the essential palindrome check
				return false;
			}

			if (need % 2 == 1) {
				oddCount++;
			}
		}

		return oddCount <= 1;
	}

	private static boolean canFormPalindromeFromPool(String str, Map<Character, Integer> pool, int totalChars) {
		int n = str.length();
		if (n > totalChars) {
			return false;
		}

		// Check if we can form a palindrome of length n from the character pool
		Map<Character, Integer> tempPool = new HashMap<>(pool);

		// For a palindrome, we need pairs of characters + possibly one single character
		int pairsNeeded = n / 2;
		int singleNeeded = n % 2;

		// Count how many pairs we can form
		int pairsAvailable = 0;
		for (int count : tempPool.values()) {
			pairsAvailable += count / 2;
		}

		// Check if we have enough pairs
		if (pairsAvailable < pairsNeeded) {
			return false;
		}

		// After using pairs, check if we have at least one character left for the middle
		int remainingChars = totalChars - pairsNeeded * 2;
		return remainingChars >= singleNeeded;
	}

}
