/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.interview.hrank.palindrom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * @author javau
 */
@Slf4j
public class FinalPalindromeCounter {

	public static void main(String[] args) {
		String[] test1 = { "abc", "def", "aba" };

		/*
		 * System.out.println("\n* Running Test 1: "); System.out.println( "1 Test 1: " +
		 * Arrays.toString(test1) + " -> " + countPossiblePalindromes(test1));
		 * System.out.println( "2 Test 1: " + Arrays.toString(test1) + " -> " +
		 * countPossiblePalindromes2(test1)); System.out.println( "3 Test 1: " +
		 * Arrays.toString(test1) + " -> " + countPossiblePalindromesMy(test1));
		 * System.out.println( "4 Test 1: " + Arrays.toString(test1) + " -> " +
		 * countPossiblePalindromesSimple(test1));
		 * 
		 * System.out.println("\n* Running Test 2: "); String[] test2 = {"a", "b", "c"};
		 * System.out.println( "1 Test 2: " + Arrays.toString(test2) + " -> " +
		 * countPossiblePalindromes(test2)); System.out.println( "2 Test 2: " +
		 * Arrays.toString(test2) + " -> " + countPossiblePalindromes2(test2));
		 * System.out.println( "3 Test 2: " + Arrays.toString(test2) + " -> " +
		 * countPossiblePalindromesMy(test2)); System.out.println( "4 Test 2: " +
		 * Arrays.toString(test2) + " -> " + countPossiblePalindromesSimple(test2));
		 * 
		 * System.out.println("\n* Running Test 3: "); String[] test3 = {"ab", "ba",
		 * "cd"}; System.out.println( "1 Test 3: " + Arrays.toString(test3) + " -> " +
		 * countPossiblePalindromes(test3)); System.out.println( "2 Test 3: " +
		 * Arrays.toString(test3) + " -> " + countPossiblePalindromes2(test3));
		 * System.out.println( "3 Test 3: " + Arrays.toString(test3) + " -> " +
		 * countPossiblePalindromesMy(test3)); System.out.println( "4 Test 3: " +
		 * Arrays.toString(test3) + " -> " + countPossiblePalindromesSimple(test3)); //
		 */

		System.out.println("\n* Running Test 4: ");
		String[] test4 = { "race", "car", "ecar" };
		System.out.println("1 Test 4: " + Arrays.toString(test4) + " -> " + countPossiblePalindromes(test4));
		System.out.println("2 Test 4: " + Arrays.toString(test4) + " -> " + countPossiblePalindromes2(test4));
		System.out.println("3 Test 4: " + Arrays.toString(test4) + " -> " + countPossiblePalindromesMy(test4));
		System.out.println("4 Test 4: " + Arrays.toString(test4) + " -> " + countPossiblePalindromesImproved(test4));
		System.out.println("5 Test 4: " + Arrays.toString(test4) + " -> " + countPossiblePalindromesSimple(test4));

		/*
		 * System.out.println("\n* Running Test 5: "); String[] test5 = {"a", "a", "a",
		 * "b"}; System.out.println( "1 Test 5: " + Arrays.toString(test5) + " -> " +
		 * countPossiblePalindromes(test5)); System.out.println( "2 Test 5: " +
		 * Arrays.toString(test5) + " -> " + countPossiblePalindromes2(test5));
		 * System.out.println( "3 Test 5: " + Arrays.toString(test5) + " -> " +
		 * countPossiblePalindromesMy(test5)); System.out.println( "4 Test 5: " +
		 * Arrays.toString(test5) + " -> " + countPossiblePalindromesSimple(test5)); //
		 */
	}

	public static int countPossiblePalindromesSimple(String[] arr) {
		int count = 0;

		String availableChars;
		for (int i = 0; i < arr.length; i++) {
			String str = arr[i];
			if (isPalindrome(str)) {
				// log.debug("*** FOUND i={} str={} count={}", i, str, count);
				count++;
				log.debug("1 countPossiblePalindromesSimple FOUND i={} str={} count={}", i, str, count);
			}
			else {
				// log.debug("Else i={} str={} count {}", i, str, count);
				availableChars = getAvailableCharacters(arr, i);
				count += canBecomePalindromeSimple(str, availableChars);
				log.debug("2 countPossiblePalindromesSimple FOUND i={} str={} availableChars {} count={}", i, str,
						availableChars, count);
			}
		}

		return count;
	}

	private static int canBecomePalindromeSimple(String str, String availableChars) {
		int count = 0;

		for (int i = 0; i < str.length(); i++) {
			for (char c : availableChars.toCharArray()) {
				String modStr = getModifiedstring(str, i, c);
				boolean found = isPalindrome(modStr);
				if (found) {
					count++;
					log.debug("canBecomePalindromeSimple str={}, i={}, availableChars={}, modStr={}, found={}, count{}",
							str, i, availableChars, modStr, found, count);
					break;
				}
			}
		}

		return count;
	}

	private static String getModifiedstring(String str, int index, char c) {
		return str.substring(0, index) + c + str.substring(index + 1);
	}

	public static int countPossiblePalindromesMy(String[] arr) {
		if (arr == null || arr.length == 0) {
			return 0;
		}

		int count = 0;

		for (int i = 0; i < arr.length; i++) {
			String currentStr = arr[i];
			String availableChars = getAvailableCharacters(arr, i);

			if (canBecomePalindromeMy(currentStr, availableChars)) {
				count++;
			}
		}

		return count;
	}

	private static boolean canBecomePalindromeMy(String str, String availableChars) {
		// Count mismatches that need to be fixed
		int mismatches = 0;
		int left = 0, right = str.length() - 1;

		while (left < right) {
			if (str.charAt(left) != str.charAt(right)) {
				boolean matched = canFindMatch(str.charAt(left), str.charAt(right), availableChars);
				if (!matched) {
					mismatches++;
				}
			}
			left++;
			right--;
		}

		// If no mismatches, it's already a palindrome
		// if (mismatches == 0) return true;
		return mismatches == 0;

		// For each mismatch, we need one replacement character
		// But the replacement character must match the character we're trying to create
		// More precise: we need to check if for each mismatch,
		// we have the required character in availableChars
		// return canFixAllMismatches(str, availableChars);
	}

	private static boolean canFindMatch(char left, char right, String availableChars) {
		for (char c : availableChars.toCharArray()) {
			if (c == left || c == right) {
				return true;
			}
		}

		return false;
	}

	public static int countPossiblePalindromes(String[] arr) {
		if (arr == null || arr.length == 0) {
			return 0;
		}

		int count = 0;

		for (int i = 0; i < arr.length; i++) {
			String currentStr = arr[i];
			String availableChars = getAvailableCharacters(arr, i);

			if (canBecomePalindrome(currentStr, availableChars)) {
				count++;
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

	private static boolean canBecomePalindrome(String str, String availableChars) {
		// If it's already a palindrome
		if (isPalindrome(str)) {
			return true;
		}

		// For each mismatched pair, check if we can fix it with available characters
		int left = 0, right = str.length() - 1;
		// We need to track which replacements we've used
		List<Character> availableList = new ArrayList<>();
		for (char c : availableChars.toCharArray()) {
			availableList.add(c);
		}

		while (left < right) {
			if (str.charAt(left) != str.charAt(right)) {
				// Characters don't match - we need to replace one of them
				char leftChar = str.charAt(left);
				char rightChar = str.charAt(right);

				// Check if we can replace left char with right char using available chars
				boolean canReplaceLeft = availableList.contains(Character.valueOf(rightChar));

				// Check if we can replace right char with left char using available chars
				boolean canReplaceRight = availableList.contains(Character.valueOf(leftChar));

				if (!canReplaceLeft && !canReplaceRight) {
					return false; // Cannot fix this mismatch
				}

				// Use one replacement
				if (canReplaceLeft) {
					// Remove the character we used for replacement
					availableList.remove(Character.valueOf(rightChar));
				}
				else {
					availableList.remove(Character.valueOf(leftChar));
				}
			}
			left++;
			right--;
		}

		return true;
	}

	private static boolean isPalindrome(String str) {
		if (str == null) {
			return false;
		}
		return new StringBuilder(str).reverse().toString().equals(str);
	}

	public static int countPossiblePalindromes2(String[] arr) {
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

	public static int countPossiblePalindromesImproved(String[] arr) {
		if (arr == null || arr.length == 0) {
			return 0;
		}

		int count = 0;

		for (int i = 0; i < arr.length; i++) {
			String currentStr = arr[i];
			if (isPalindrome(currentStr)) {
				count++;
				log.debug("1 countPossiblePalindromesImproved FOUND i={} str={} count={}", i, currentStr, count);
			}
			else {
				String availableChars = getAvailableCharacters(arr, i);
				if (canBecomePalindromeImproved(currentStr, availableChars)) {
					count++;
				}
				log.debug("2 countPossiblePalindromesImproved FOUND i={} str={} availableChars {} count={}", i,
						currentStr, availableChars, count);
			}
		}

		return count;
	}

	private static boolean canBecomePalindromeImproved(String str, String availableChars) {
		// Count mismatches that need to be fixed
		int mismatches = 0;
		int left = 0, right = str.length() - 1;

		while (left < right) {
			if (str.charAt(left) != str.charAt(right)) {
				mismatches++;
			}
			left++;
			right--;
		}

		// If no mismatches, it's already a palindrome
		if (mismatches == 0) {
			return true;
		}

		// For each mismatch, we need one replacement character
		// But the replacement character must match the character we're trying to create
		// More precise: we need to check if for each mismatch,
		// we have the required character in availableChars
		return canFixAllMismatches(str, availableChars);
	}

	private static boolean canFixAllMismatches(String str, String availableChars) {
		// Create frequency map of available characters
		int[] availableFreq = new int[256]; // Extended ASCII
		for (char c : availableChars.toCharArray()) {
			availableFreq[c]++;
		}

		int left = 0, right = str.length() - 1;

		while (left < right) {
			if (str.charAt(left) != str.charAt(right)) {
				// We need to make these characters match
				// We can either replace left with right, or right with left
				char neededChar1 = str.charAt(right); // To replace left char
				char neededChar2 = str.charAt(left); // To replace right char

				if (availableFreq[neededChar1] > 0) {
					availableFreq[neededChar1]--; // Use this character
				}
				else if (availableFreq[neededChar2] > 0) {
					availableFreq[neededChar2]--; // Use this character
				}
				else {
					return false; // Cannot fix this mismatch
				}
			}
			left++;
			right--;
		}

		log.debug("canFixAllMismatches return true str={} availableChars={} availableFreq={}", str, availableChars,
				availableFreq);
		return true;
	}

}
