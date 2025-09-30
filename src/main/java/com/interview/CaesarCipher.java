/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.interview;

import java.util.Scanner;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CaesarCipher {

    public static void main(String[] args) {
        String text = "Hello, World!";
        int shift = 3;
        System.out.println("1 Starting CaesarCipher Original text: " + text);
        run1(text, shift);

        System.out.println("2 Starting CaesarCipher Original text: " + text);
        runX(text, shift);
        // scanner();
        System.out.println("3 Starting CaesarCipher Original text: " + text);
        runX2(text, shift);
        cipher(text, shift);

        System.out.println("4 Starting CaesarCipher Original text: " + text);
        cipher(text, 13);
        SimpleROT13Impl(text);
    }

    public static String encryptImpX(String text, int shift) {
        StringBuilder result = new StringBuilder();

        for (char ch : text.toCharArray()) {
            if (Character.isLetter(ch)) {
                char base = Character.isLowerCase(ch) ? 'a' : 'A';
                ch = (char) (((ch - base + shift) % 26) + base);
            }
            result.append(ch);
        }

        return result.toString();
    }

    public static String encryptImpX2(String text, int shift) {
        return text.chars()
                .map(
                        c -> {
                            if (Character.isLetter(c)) {
                                char base = Character.isUpperCase(c) ? 'A' : 'a';
                                return (char) ((c - base + shift) % 26) + base;
                            }
                            return c;
                        })
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    public static void cipher(String text, int shift) {
        log.info("cipher Original text:{} shift:{} ", text, shift);
        String encrypted
                = text.chars()
                        .map(
                                c -> {
                                    if (!Character.isLetter(c)) {
                                        return c;
                                    } else if (Character.isUpperCase(c)) {
                                        return (c + shift - 'A') % 26 + 'A';
                                    } else {
                                        return (c + shift - 'a') % 26 + 'a';
                                    }
                                })
                        .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                        .toString();
        log.info("cipher Encrypted:{} ", encrypted);
    }

    public static void SimpleROT13Impl(String text) {
        // String text = "hello";
        String rot13
                = text.chars()
                        .map(
                                c -> {
                                    if (c >= 'a' && c <= 'z') {
                                        return (c - 'a' + 13) % 26 + 'a';
                                    } else if (c >= 'A' && c <= 'Z') {
                                        return (c - 'A' + 13) % 26 + 'A';
                                    }
                                    return c;
                                })
                        .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                        .toString();
        System.out.println("SimpleROT13Impl:" + rot13); // uryyb
    }

    private static void run1(String text, int shift) {
        System.out.println("Original text: " + text);

        String encrypted = encrypt(text, shift);
        System.out.println("Encrypted text: " + encrypted);

        String decrypted = decrypt(encrypted, shift);
        System.out.println("Decrypted text: " + decrypted);
    }

    private static void runX(String text, int shift) {
        System.out.println("Original text: " + text);

        String encrypted = encryptX(text, shift);
        System.out.println("Encrypted text: " + encrypted);

        String decrypted = decryptX(encrypted, shift);
        System.out.println("Decrypted text: " + decrypted);
    }

    private static void runX2(String text, int shift) {
        System.out.println("Original text: " + text);

        String encrypted = encryptX2(text, shift);
        System.out.println("Encrypted text: " + encrypted);

        String decrypted = decrypt(encrypted, shift);
        System.out.println("Decrypted text: " + decrypted);
    }

    public static String encryptX2(String text, int shift) {
        StringBuilder sb = new StringBuilder();

        for (char c : text.toCharArray()) {
            sb.append(findShiftChar(c, shift));
        }

        return sb.toString();
    }

    public static char findShiftChar(char c, int shift) {
        if (!Character.isLetter(c)) {
            return c;
        }

        char shiftBaseChar = findShiftBaseChar(c);
        char rtnC = (char) (((c - shiftBaseChar + shift) % 26) + shiftBaseChar);
        // System.out.println("original c=" + c + "-shiftBaseChar=" + shiftBaseChar + "-rtnC=" + rtnC);
        return rtnC;
    }

    public static char findShiftBaseChar(char c) {
        return Character.isUpperCase(c) ? 'A' : 'a';
    }

    // Encrypts text using a shift value
    public static String encrypt(String text, int shift) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {
            char currentChar = text.charAt(i);

            if (Character.isUpperCase(currentChar)) {
                char ch = (char) (((currentChar - 'A' + shift) % 26) + 'A');
                result.append(ch);
            } else if (Character.isLowerCase(currentChar)) {
                char ch = (char) (((currentChar - 'a' + shift) % 26) + 'a');
                result.append(ch);
            } else {
                result.append(currentChar); // Non-alphabet characters remain unchanged
            }
        }

        return result.toString();
    }

    // Encrypts text using a shift value
    static int shift = 3;
    static String charStringUpper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    static String charStringLower = "abcdefghijklmnopqrstuvwxyz";

    public static String encryptHardCode(String text) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {
            char currentChar = text.charAt(i);

            if (Character.isUpperCase(currentChar)) {
                int ndx = charStringUpper.indexOf(currentChar);
                result.append(charStringUpper.charAt(ndx));
            } else if (Character.isLowerCase(currentChar)) {
                int ndx = charStringLower.indexOf(currentChar);
                result.append(charStringLower.charAt(ndx));
            } else {
                result.append(currentChar); // Non-alphabet characters remain unchanged
            }
        }

        return result.toString();
    }

    // Decrypts text using a shift value
    public static String decrypt(String text, int shift) {
        return encrypt(text, 26 - shift); // Decryption is just encryption with the inverse shift
    }

    public static void scanner() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Caesar Cipher Program");
        System.out.println("1. Encrypt text");
        System.out.println("2. Decrypt text");
        System.out.println("3. Brute force attack");
        System.out.print("Choose an option: ");

        int option = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        if (option == 1 || option == 2) {
            System.out.print("Enter text: ");
            String text = scanner.nextLine();

            System.out.print("Enter shift value (0-25): ");
            int shift = scanner.nextInt();

            if (option == 1) {
                String encrypted = encrypt(text, shift);
                System.out.println("Encrypted text: " + encrypted);
            } else {
                String decrypted = decrypt(text, shift);
                System.out.println("Decrypted text: " + decrypted);
            }
        } else if (option == 3) {
            System.out.print("Enter encrypted text: ");
            String encryptedText = scanner.nextLine();
            bruteForceAttack(encryptedText);
        } else {
            System.out.println("Invalid option!");
        }

        scanner.close();
    }

    public static String encryptX(String text, int shift) {
        StringBuilder result = new StringBuilder();

        for (char character : text.toCharArray()) {
            if (Character.isLetter(character)) {
                char base = Character.isUpperCase(character) ? 'A' : 'a';
                char encryptedChar = (char) (((character - base + shift) % 26) + base);
                result.append(encryptedChar);
            } else {
                result.append(character);
            }
        }

        return result.toString();
    }

    public static String decryptX(String text, int shift) {
        return encryptX(text, 26 - (shift % 26));
    }

    public static void bruteForceAttack(String encryptedText) {
        System.out.println("Brute force attack results:");
        for (int shift = 0; shift < 26; shift++) {
            String decrypted = decryptX(encryptedText, shift);
            System.out.println("Shift " + shift + ": " + decrypted);
        }
    }
}
