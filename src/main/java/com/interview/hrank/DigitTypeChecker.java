/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.interview.hrank;

/**
 * @author javau
 */
public class DigitTypeChecker {

    public static void checkDigitType(char ch) {
        boolean isDecimal = (ch >= '0' && ch <= '9');
        boolean isHex
                = ((ch >= '0' && ch <= '9') || (ch >= 'A' && ch <= 'F') || (ch >= 'a' && ch <= 'f'));

        // OR using Character methods:
        boolean isDecimal2 = Character.isDigit(ch);
        boolean isHex2 = Character.getNumericValue(ch) >= 0 && Character.getNumericValue(ch) < 16;

        System.out.println("'" + ch + "' is decimal: " + isDecimal);
        System.out.println("'" + ch + "' is hex: " + isHex);
    }

    // Check if character is decimal digit (0-9)
    public static boolean isDecimalDigit(char ch) {
        return ch >= '0' && ch <= '9';
        // OR: return Character.isDigit(ch);
    }

    // Check if character is hexadecimal digit (0-9, A-F, a-f)
    public static boolean isHexDigit(char ch) {
        int value = Character.getNumericValue(ch);
        return value >= 0 && value < 16;
        // OR: return "0123456789ABCDEFabcdef".indexOf(ch) != -1;
    }

    // Check if character is octal digit (0-7)
    public static boolean isOctalDigit(char ch) {
        return ch >= '0' && ch <= '7';
    }

    // Check if character is binary digit (0-1)
    public static boolean isBinaryDigit(char ch) {
        return ch == '0' || ch == '1';
    }

    // Get the maximum base this digit can represent
    public static int getMaxBase(char ch) {
        int value = Character.getNumericValue(ch);
        if (value == -1) {
            return 0; // Not a digit in any base
        }
        if (value < 2) {
            return 2; // Binary (0-1)
        }
        if (value < 8) {
            return 8; // Octal (0-7)
        }
        if (value < 10) {
            return 10; // Decimal (0-9)
        }
        return 36; // Base-36 (0-9, A-Z)
    }
}
