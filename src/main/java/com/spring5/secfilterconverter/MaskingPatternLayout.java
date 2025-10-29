/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.secfilterconverter;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;

import java.util.regex.Pattern;

public class MaskingPatternLayout extends PatternLayout {

    private static final Pattern[] PHI_PATTERNS = {
        Pattern.compile("(\"ssn\"\\s*:\\s*\")([^\"]+)(\")", Pattern.CASE_INSENSITIVE),
        Pattern.compile("(\"socialSecurityNumber\"\\s*:\\s*\")([^\"]+)(\")", Pattern.CASE_INSENSITIVE),
        Pattern.compile("(\"email\"\\s*:\\s*\")([^\"]+)(\")", Pattern.CASE_INSENSITIVE),
        Pattern.compile("(\"phone\"\\s*:\\s*\")([^\"]+)(\")", Pattern.CASE_INSENSITIVE),
        Pattern.compile("(\"dateOfBirth\"\\s*:\\s*\")([^\"]+)(\")", Pattern.CASE_INSENSITIVE),
        Pattern.compile("(\\b\\d{3}-\\d{2}-\\d{4}\\b)"), // SSN pattern
        Pattern.compile("(\\b\\w+@\\w+\\.\\w+\\b)"), // Email pattern
        Pattern.compile("(\\b\\d{3}-\\d{3}-\\d{4}\\b)") // Phone pattern
    };

    @Override
    public String doLayout(ILoggingEvent event) {
        String message = super.doLayout(event);
        return maskPHIData(message);
    }

    private String maskPHIData(String message) {
        if (message == null) {
            return null;
        }

        String maskedMessage = message;
        for (Pattern pattern : PHI_PATTERNS) {
            maskedMessage = pattern.matcher(maskedMessage).replaceAll(match -> {
                if (match.groupCount() == 3) {
                    // JSON field pattern
                    return match.group(1) + "***MASKED***" + match.group(3);
                } else {
                    // Direct value pattern
                    return "***MASKED***";
                }
            });
        }
        return maskedMessage;
    }
}
