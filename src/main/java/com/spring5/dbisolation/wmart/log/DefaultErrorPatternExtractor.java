/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.wmart.log;

import java.util.Arrays;
import java.util.Set;

/**
 * @author javau
 */
public class DefaultErrorPatternExtractor implements ErrorPatternExtractor {

    private static final Set<String> COMMON_EXCEPTIONS
            = Set.of(
                    "NullPointerException",
                    "IllegalArgumentException",
                    "IOException",
                    "SQLException",
                    "TimeoutException",
                    "RuntimeException");

    @Override
    public String extractPattern(String logMessage) {
        if (logMessage == null || logMessage.trim().isEmpty()) {
            return null;
        }

        // Try to extract exception name
        for (String exception : COMMON_EXCEPTIONS) {
            if (logMessage.contains(exception)) {
                return exception;
            }
        }

        // Fallback: extract first meaningful part
        return extractFallbackPattern(logMessage);
    }

    private String extractFallbackPattern(String message) {
        String cleanMessage = message.replaceAll("[^a-zA-Z0-9\\s]", " ");
        String[] words = cleanMessage.split("\\s+");

        return Arrays.stream(words)
                .filter(word -> word.length() > 4)
                .filter(word -> Character.isUpperCase(word.charAt(0)))
                .findFirst()
                .orElse("UnknownError");
    }
}
