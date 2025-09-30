/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.interview.hrank;

import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author javau
 */
public class AlphanumericSumCalculator {

    /*
  Key Features:
      Handles both uppercase and lowercase letters
      Ignores non-alphanumeric characters
      Uses proper character conversion methods
      Includes test cases for verification
      User-friendly input/output interface
  The program efficiently processes the string and calculates the sum according to the specified rules.
     */
    public static void main(String[] args) {
        List<String> list = List.of("a1b2c3", "hello123", "ABCxyz");
        /*
    Input string: a1b2c3
    Calculated sum: 12 (a=1 + 1 + b=2 + 2 + c=3 + 3)

    Input string: hello123
    Calculated sum: 58 (h=8 + e=5 + l=12 + l=12 + o=15 + 1 + 2 + 3)

    Input string: ABCxyz
    Calculated sum: 81 (A=1 + B=2 + C=3 + x=24 + y=25 + z=26)
         */
        for (String s : list) {
            System.out.println("1. Input " + s + "-calculated=" + calculateAlphanumericSum(s));
            System.out.println("2. Input " + s + "-calculated=" + calculateAlphanumericSumImproved(s));
            System.out.println("3. Input " + s + "-calculated=" + calculateAlphanumericSumStream(s));
        }
    }

    private static void scanner() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Alphanumeric String Sum Calculator");
        System.out.println("==================================");
        System.out.print("Enter an alphanumeric string: ");

        String input = scanner.nextLine();

        int result = calculateAlphanumericSum(input);

        System.out.println("Input string: " + input);
        System.out.println("Calculated sum: " + result);

        // Additional test cases
        System.out.println("\nTest cases:");
        System.out.println("a1b2c3 -> " + calculateAlphanumericSum("a1b2c3"));
        System.out.println("hello123 -> " + calculateAlphanumericSum("hello123"));
        System.out.println("ABCxyz -> " + calculateAlphanumericSum("ABCxyz"));
        System.out.println("Test123!@# -> " + calculateAlphanumericSum("Test123!@#"));

        scanner.close();
    }

    /*
  How the program works:
      Input Handling: The program reads an alphanumeric string from the user.
      Character Processing: It processes each character in the string:
          Digits: Added directly to the sum using Character.getNumericValue()
          Letters: Converted to their position in the alphabet (a=1, b=2, ..., z=26)
          Non-alphanumeric characters: Ignored
      Case Handling: The program handles both uppercase and lowercase letters by converting them to lowercase first.
      Output: Displays the original string and the calculated sum.
     */
    public static int calculateAlphanumericSum(String input) {
        int sum = 0;

        for (int i = 0; i < input.length(); i++) {
            char currentChar = input.charAt(i);

            if (Character.isDigit(currentChar)) {
                // Add digit as is (convert char to numeric value)
                sum += Character.getNumericValue(currentChar);
            } else if (Character.isLetter(currentChar)) {
                // Convert letter to its position in alphabet (case-insensitive)
                char lowerCaseChar = Character.toLowerCase(currentChar);
                int letterValue = lowerCaseChar - 'a' + 1;
                sum += letterValue;
            }
            // Ignore non-alphanumeric characters
        }

        return sum;
    }

    public static int calculateAlphanumericSumImproved(String input) {
        int sum = 0;
        input = input.toLowerCase();

        for (char c : input.toCharArray()) {
            if (Character.isDigit(c)) {
                sum += Character.getNumericValue(c);
            } else if (Character.isLetter(c)) {
                sum += (c - 'a' + 1);
            }
        }

        return sum;
    }

    public static int calculateAlphanumericSumStream(String input) {
        AtomicInteger sum = new AtomicInteger(0);
        input
                .toLowerCase()
                .chars()
                .mapToObj(c -> (char) c)
                .forEach(
                        c -> {
                            if (Character.isDigit(c)) {
                                sum.addAndGet(Character.getNumericValue(c));
                            } else if (Character.isLetter(c)) {
                                sum.addAndGet((c - 'a' + 1));
                            }
                        });

        return sum.get();
    }
}
