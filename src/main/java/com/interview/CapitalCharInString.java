/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.interview;

import java.util.Arrays;
import java.util.List;
import java.util.OptionalInt;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author javaugi
 */
public class CapitalCharInString {

	private static final CapitalCharInString main = new CapitalCharInString();

	public static void main(String[] args) {
		// main.test1();
		// main.test2();
		convertDigitsToWords();
		removeVowels();
		convertToAscii();
		customCharTrans();
	}

	public static void SimpleROT13Impl() {
		String text = "hello";
		String rot13 = text.chars().map(c -> {
			if (c >= 'a' && c <= 'z') {
				return (c - 'a' + 13) % 26 + 'a';
			}
			else if (c >= 'A' && c <= 'Z') {
				return (c - 'A' + 13) % 26 + 'A';
			}
			return c;
		}).collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();
		System.out.println(rot13); // uryyb
	}

	public static void customCharTrans() {
		String text = "hello123";
		String transformed = text.chars().map(c -> {
			if (Character.isLetter(c)) {
				return Character.toUpperCase(c);
			}
			else if (Character.isDigit(c)) {
				return c + 1; // increment digit
			}
			return c;
		}).collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();
		System.out.println(transformed); // HELLO234
	}

	public static void convertDigitsToWords() {
		String numbers = "123";
		String words = numbers.chars().map(c -> {
			switch (c) {
				case '1':
					return "one".chars().sum();
				case '2':
					return "two".chars().sum();
				case '3':
					return "three".chars().sum();
				default:
					return c;
			}
		}).mapToObj(c -> String.valueOf((char) c)).collect(Collectors.joining());

		System.out.println(words); // onetwothree
	}

	public static void removeVowels() {
		String text = "hello world";
		String noVowels = text.chars()
			.filter(c -> "aeiouAEIOU".indexOf(c) == -1)
			.map(c -> c) // identity map
			.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
			.toString();
		System.out.println(noVowels); // hll wrld
	}

	public static void convertToAscii() {
		String text = "abc";
		int[] asciiValues = text.chars()
			.map(c -> c) // already ASCII values
			.toArray();
		System.out.println("1 convertToAscii:" + Arrays.toString(asciiValues)); // [97,
																				// 98, 99]
		System.out.println("2 convertToAscii:" + Arrays.toString(text.chars().toArray())); // [97,
																							// 98,
																							// 99]
	}

	private void test1() {
		String word = "This is the Test to Check How many Capital Chars are";

		long count = word.chars()
			.filter(Character::isUpperCase)
			// .filter(Character::isLetter)
			// .filter(Character::isDigit)
			// .filter(c -> !Character.isWhitespace(c))
			.count();
		System.out.println("There are total " + count + " capital characters in the string: " + word);

		int shift = 5;
		String upper = word.chars()
			// .map(c -> c + shift)
			// .map(Character::toLowerCase)
			.map(Character::toUpperCase)
			.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
			.toString();
		System.out.println("UPPER: " + upper + " \n original " + word);

		// Collect into a String
		String onlyDigits = word.chars()
			.filter(Character::isDigit)
			.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
			.toString();

		// Collect into a List<Character>
		List<Character> charsList = word.chars().mapToObj(c -> (char) c).collect(Collectors.toList());

		int sum = word.chars().sum(); // sum of code points
		OptionalInt max = word.chars().max();
		OptionalInt min = word.chars().min();
		double avg = word.chars().average().orElse(0);

		final AtomicInteger counter = new AtomicInteger(0);
		word.chars().forEach(c -> {
			if (Character.isUpperCase(c)) {
				// do something
				// counter.addAndGet(1);
				counter.incrementAndGet();
			}
		});
		System.out.println("There are total " + counter.get() + " capital characters in the string: " + word);

		int capitalCharCount = 0;
		for (char ch : word.toCharArray()) {
			if (Character.isUpperCase(ch)) {
				capitalCharCount++;
			}
		}
		System.out.println("There are total " + capitalCharCount + " capital characters in the string: " + word);

		char char1 = 'A';
		char char2 = 'a';
		char char3 = '1';
		char char4 = '$';

		// Method 1: Using Character.isUpperCase()
		System.out.println(char1 + " is uppercase: " + Character.isUpperCase(char1)); // true
		System.out.println(char2 + " is uppercase: " + Character.isUpperCase(char2)); // false
		System.out.println(char3 + " is uppercase: " + Character.isUpperCase(char3)); // false
		System.out.println(char4 + " is uppercase: " + Character.isUpperCase(char4)); // false

		// Method 2: Using comparison with ASCII values (less preferred, but shows how it
		// works)
		// Uppercase letters in ASCII are between 'A' (65) and 'Z' (90) inclusive.
		System.out.println(char1 + " is uppercase: " + (char1 >= 'A' && char1 <= 'Z')); // true
		System.out.println(char2 + " is uppercase: " + (char2 >= 'A' && char2 <= 'Z')); // false
		System.out.println(char3 + " is uppercase: " + (char3 >= 'A' && char3 <= 'Z')); // false
		System.out.println(char4 + " is uppercase: " + (char4 >= 'A' && char4 <= 'Z')); // false

		// Example Usage in a loop
		String testString = "Hello World!";
		for (int i = 0; i < testString.length(); i++) {
			char c = testString.charAt(i);
			if (Character.isUpperCase(c)) {
				System.out.println(c + " is an uppercase letter.");
			}
			else {
				System.out.println(c + " is not an uppercase letter.");
			}
		}
	}

	private void test2() {
		char[] a = { 'a', 'b', 'c', 'd', 'e', 'A', 'B', 'C', 'D', 'E' };

		int lowerCaseCount = 0;
		int upperCaseCount = 0;
		for (char ch : a) {
			if (Character.isUpperCase(ch)) {
				upperCaseCount++;
			}
			else {
				lowerCaseCount++;
			}
		}
		System.out.println("The array length has " + upperCaseCount + " upper case and " + lowerCaseCount
				+ " lower case and the total is " + (lowerCaseCount + lowerCaseCount));
	}

}
