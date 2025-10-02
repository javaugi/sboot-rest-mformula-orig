/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.wmart.log;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public class LogAnalyzer {

	/**
	 * Analyzes error frequency from log entries with focus on: - Code readability -
	 * Efficiency considerations - Handling large datasets - Edge cases (empty input, null
	 * values)
	 */
	public Map<String, Integer> analyzeErrorFrequency(List<LogEntry> logs) {
		// Edge case: null or empty input
		if (logs == null || logs.isEmpty()) {
			return Collections.emptyMap();
		}

		// Use streams for readability and parallel processing capability
		return logs.parallelStream() // Parallel stream for large datasets
			.filter(Objects::nonNull) // Filter out null log entries
			.filter(LogEntry::isError) // Only process ERROR level logs
			.map(LogEntry::getMessage)
			.filter(Objects::nonNull) // Filter out null messages
			.filter(message -> !message.trim().isEmpty()) // Filter empty messages
			.map(this::extractErrorPattern) // Extract error pattern (e.g., exception
											// type)
			.filter(Objects::nonNull) // Filter null patterns
			.collect(Collectors.toConcurrentMap(Function.identity(), // Key: error pattern
					pattern -> 1, // Value: count 1 for each occurrence
					Integer::sum, // Merge function: sum counts
					ConcurrentHashMap::new // Thread-safe map for parallel streams
			));
	}

	/**
	 * Extracts meaningful error patterns from log messages Example:
	 * "java.lang.NullPointerException: Cannot invoke method on null" ->
	 * "NullPointerException"
	 */
	private String extractErrorPattern(String logMessage) {
		if (logMessage == null || logMessage.trim().isEmpty()) {
			return null;
		}

		// Pattern 1: Extract exception class name
		String[] parts = logMessage.split(":", 2);
		if (parts.length > 0) {
			String firstPart = parts[0].trim();

			// Check if it contains exception class name
			if (firstPart.contains(".") && (firstPart.endsWith("Exception") || firstPart.endsWith("Error"))) {
				// Extract just the class name without package
				int lastDotIndex = firstPart.lastIndexOf('.');
				if (lastDotIndex != -1 && lastDotIndex < firstPart.length() - 1) {
					return firstPart.substring(lastDotIndex + 1);
				}
				return firstPart;
			}
		}

		// Pattern 2: Extract error codes (e.g., "ERROR_404", "ERR-500")
		if (logMessage.matches(".*[A-Z]+_?\\d{3,}.*")) {
			java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("([A-Z]+_?\\d{3,})").matcher(logMessage);
			if (matcher.find()) {
				return matcher.group(1);
			}
		}

		// Pattern 3: For custom error messages, take first few meaningful words
		return extractKeyPhrase(logMessage);
	}

	private String extractKeyPhrase(String message) {
		String[] words = message.split("\\s+");
		List<String> meaningfulWords = Arrays.stream(words)
			.filter(word -> word.length() > 3) // Filter short words
			.filter(word -> !isCommonWord(word)) // Filter common words
			.limit(3) // Take first 3 meaningful words
			.collect(Collectors.toList());

		return meaningfulWords.isEmpty() ? "UnknownError" : String.join(" ", meaningfulWords);
	}

	private boolean isCommonWord(String word) {
		Set<String> commonWords = Set.of("the", "and", "for", "with", "from", "this", "that");
		return commonWords.contains(word.toLowerCase());
	}

}
