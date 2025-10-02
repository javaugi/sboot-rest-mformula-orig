/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.interview.hrank.cap1;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author javaugi
 */
public class Code2MediumStringProcessing {

	/*
	 * Problem 2: Medium – String Processing Task: Given a string s, find the length of
	 * the longest substring with at most two distinct characters.
	 * 
	 * Example: Input: "ccaabbb" → Output: 5 ("aabbb")
	 * 
	 * Solution (Sliding Window):
	 * 
	 * Key Points:
	 * 
	 * Sliding window + hashmap to track character counts.
	 * 
	 * O(N) time, O(1) space (since map size ≤ 3).
	 */
	public static void main(String[] args) {
		Code2MediumStringProcessing main = new Code2MediumStringProcessing();
		String input = "Accaabbb";
		longestSubstringWithAtLeastTwoDistinctCharsMy(input);
		if (true) {
			return;
		}

		int result = main.longestSubstringWithAtLeastTwoDistinctChars(input);
		System.out.println("1 The result is: " + result + " from input: " + input);

		input = "dkfkflfhf";
		result = main.longestSubstringWithAtLeastTwoDistinctChars(input);
		System.out.println("2 The result is: " + result + " from input: " + input);

		input = "cfdfehgf";
		result = main.longestSubstringWithAtLeastTwoDistinctChars(input);
		System.out.println("3 The result is: " + result + " from input: " + input);

		/*
		 * --- exec-maven-plugin:3.1.0:exec (default-cli) @ cimathformulas --- 1 The
		 * result is: 5 from input: ccaabbb 2 The result is: 4 from input: dkfkflfhf 3 The
		 * result is: 3 from input: cfdfehgf ------------------------------------------
		 */
	}

	public static void longestSubstringWithAtLeastTwoDistinctCharsMy(String s) {
		Map<Character, Integer> map = new HashMap<>();
		int left = 0, maxLen = 0;

		/*
		 * In summary: ASCII value: Obtain by casting a char to int. This gives you the
		 * character's numerical representation in the ASCII/Unicode table. Numerical
		 * value (for digits): Obtain by subtracting the ASCII value of '0' from the
		 * char's ASCII value. This gives you the actual integer value represented by the
		 * digit character.
		 */

		/*
		 * Key Points Summary: 1. Java char is a 16-bit unsigned integer (0-65,535) 2.
		 * ASCII values range from 0-127 3. Type casting: (int)char gets ASCII value,
		 * (char)int gets character 4. Character arithmetic: chars can be used in
		 * mathematical operations 5. Digit conversion: charDigit - '0' converts digit
		 * characters to integers 6. Case conversion: Add/subtract 32 for ASCII case
		 * changes 7. Unicode support: Java supports international characters beyond ASCII
		 */
		for (char c : s.toCharArray()) {
			int zeroValue = '0';
			int asciiValue = c;
			int intValue = c - '0';
			System.out.println("zeroValue=" + zeroValue + "-char=" + c + "-asciiValue=" + asciiValue + "-cast int="
					+ (int) c + "-intValue=" + intValue + "-numeric value=" + Character.getNumericValue(c));
			// Character.getNumericValue() returns the numeric value that the character
			// represents in its
			// number system, not necessarily base-10.
		}

		Map<Integer, Long> occurrences = s.chars()
			.boxed()
			.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

		occurrences.keySet().stream().forEach(k -> {
			System.out.println("    key=" + k + "-value=" + occurrences.get(k));
		});

		char[] hexChars = { 'A', 'F', '9', 'G', '1' };
		for (char hex : hexChars) {
			System.out.println("'" + hex + "' is hex digit: " + isHexDigit(hex));
		}

		char ch1 = '1';
		System.out.println("Character: " + ch1);
		System.out.println("ASCII value (position in table): " + (int) ch1); // 65
		System.out.println("Numeric value (as a base 16? digit): " + Character.getNumericValue(ch1)); // 10
		System.out.println("Base 10 Numeric value (as a base 10 ? digit): " + (ch1 - '0'));

		char ch = 'A';
		System.out.println("Character: " + ch);
		System.out.println("ASCII value (position in table): " + (int) ch); // 65
		System.out.println("Numeric value (as a base 16? digit): " + Character.getNumericValue(ch)); // 10
		System.out.println("Base 10 Numeric value (as a base 10 ? digit): " + (ch - '0'));

		char ch2 = 'a';
		System.out.println("Character: " + ch2);
		System.out.println("ASCII value (position in table): " + (int) ch2); // 65
		System.out.println("Numeric value (as a base 16? digit): " + Character.getNumericValue(ch2)); // 10
		System.out.println("Base 10 Numeric value (as a base 10 ? digit): " + (ch2 - '0'));

		System.out.println("\n--- Explanation ---");
		System.out.println("ASCII value 65: 'A' is the 65th character in ASCII table");
		System.out.println("Numeric value 10: In hexadecimal, 'A' = 10, 'B' = 11, ..., 'F' = 15");
	}

	// Method to check if character is valid hexadecimal digit
	public static boolean isHexDigit(char ch) {
		return Character.getNumericValue(ch) != -1 && Character.getNumericValue(ch) < 16;
	}

	public int longestSubstringWithAtLeastTwoDistinctChars(String s) {
		Map<Character, Integer> map = new HashMap<>();
		int left = 0, maxLen = 0;

		for (int right = 0; right < s.length(); right++) {
			char c = s.charAt(right);
			map.put(c, map.getOrDefault(c, 0) + 1);
			left = processMapReducer(map, s, left);
			maxLen = Math.max(maxLen, right - left + 1);
		}
		return maxLen;
	}

	private int processMapReducer(Map<Character, Integer> map, String s, int left) {
		while (map.size() > 2 && left < s.length()) {
			char leftChar = s.charAt(left);
			map.put(leftChar, map.get(leftChar) - 1);
			if (map.get(leftChar) == 0) {
				map.remove(leftChar);
			}
			left++;
		}
		return left;
	}

}
