/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.interview.hrank;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ArrayOccuranceMax {

	private static final ArrayOccuranceMax main = new ArrayOccuranceMax();

	public static void main(String[] args) {
		int[] arr = { 1, 2, 2, 2, 2, 3, 5, 6, 7, 8, 3, 2 };
		log.info("1 Starting ...");
		main.usingStreams1(arr);
		log.info("1 Done usingStream1");
		main.usingStreams2(arr);
		log.info("1 Done");

		int[] arr2 = { 1, 2, 2, 2, 2, 3, 3, 3, 3 };
		log.info("\n 2 Starting ...");
		main.usingStreams1(arr2);
		log.info("2 Done usingStream1");
		main.usingStreams2(arr2);
		log.info("Done usingStream2 \n");
		main.usingStreams3(arr2);
		log.info("2 Done usingStream3");
		main.usingStreams4(arr2);
		log.info("2 Done usingStream4 \n");
		main.usingStreams5(arr2);
		log.info("2 All Done");
	}

	private void usingStreams1(int[] numbers) {
		// Using Java 8 streams to find occurrences
		Map<Integer, Long> occurrences = Arrays.stream(numbers)
			.boxed()
			.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

		// Find element with max occurrences
		Optional<Map.Entry<Integer, Long>> maxEntry = occurrences.entrySet().stream().max(Map.Entry.comparingByValue());

		log.info("Occurrences: " + occurrences);

		if (maxEntry.isPresent()) {
			log.info("Element with max occurrences:{} Count:{}", maxEntry.get().getKey(), maxEntry.get().getValue());
		}
		else {
			log.info("No elements found");
		}
	}

	private void usingStreams2(int[] numbers) {
		Optional<Map.Entry<Integer, Long>> maxEntry = Arrays.stream(numbers)
			.boxed()
			.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
			.entrySet()
			.stream()
			.max(Map.Entry.comparingByValue());

		if (maxEntry.isPresent()) {
			log.info("Element with max occurrences:{} Count:{}", maxEntry.get().getKey(), maxEntry.get().getValue());
		}
		else {
			log.info("No elements found");
		}
	}

	private void usingStreams3(int[] numbers) {
		// Using Java 8 streams to find occurrences
		Map<Integer, Long> occurrences = Arrays.stream(numbers)
			.boxed()
			.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

		log.info("Occurrences: " + occurrences);

		// Find the maximum occurrence count
		Optional<Long> maxCount = occurrences.values().stream().max(Long::compare);

		if (maxCount.isPresent()) {
			long maxValue = maxCount.get();

			// Find all elements with the maximum occurrence count
			List<Integer> maxElements = occurrences.entrySet()
				.stream()
				.filter(entry -> entry.getValue() == maxValue)
				.map(Map.Entry::getKey)
				.collect(Collectors.toList());

			if (maxElements.size() == 1) {
				log.info("Element with max occurrences: {} Count: {}", maxElements.get(0), maxValue);
			}
			else {
				log.info("Elements with max occurrences: {} Count: {}", maxElements, maxValue);
			}
		}
		else {
			log.info("No elements found");
		}
	}

	private void usingStreams4(int[] numbers) {
		// Using Java 8 streams to find occurrences
		Map<Integer, Long> occurrences = Arrays.stream(numbers)
			.boxed()
			.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

		log.info("Occurrences: " + occurrences);

		// Find the maximum count first
		Optional<Long> maxCountOpt = occurrences.values().stream().max(Long::compare);

		if (maxCountOpt.isPresent()) {
			long maxCount = maxCountOpt.get();

			// Find all entries with the maximum count
			List<Map.Entry<Integer, Long>> maxEntries = occurrences.entrySet()
				.stream()
				.filter(entry -> entry.getValue() == maxCount)
				.collect(Collectors.toList());

			if (maxEntries.size() == 1) {
				Map.Entry<Integer, Long> entry = maxEntries.get(0);
				log.info("Element with max occurrences: {} Count: {}", entry.getKey(), entry.getValue());
			}
			else {
				log.info("Elements with max occurrences:");
				maxEntries.forEach(entry -> log.info("  Element: {} Count: {}", entry.getKey(), entry.getValue()));
			}
		}
		else {
			log.info("No elements found");
		}
	}

	private void usingStreams5(int[] numbers) {
		// Using Java 8 streams to find occurrences
		Map<Integer, Long> occurrences = Arrays.stream(numbers)
			.boxed()
			.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

		log.info("Occurrences: " + occurrences);

		// Find the maximum count first
		Optional<Long> maxCountOpt = occurrences.values().stream().max(Long::compare);

		if (maxCountOpt.isPresent()) {
			long maxCount = maxCountOpt.get();

			// Find all entries with the maximum count
			List<Map.Entry<Integer, Long>> maxEntries = occurrences.entrySet()
				.stream()
				.filter(entry -> entry.getValue() == maxCount)
				.collect(Collectors.toList());

			maxEntries.forEach(entry -> log.info("  Element: {} Count: {}", entry.getKey(), entry.getValue()));
		}
		else {
			log.info("No elements found");
		}
	}

	private void usingStreams6(int[] numbers) {
		// Using Java 8 streams to find occurrences
		Map<Integer, Long> occurrences = Arrays.stream(numbers)
			.boxed()
			.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

		// Find element with max occurrences
		Optional<Map.Entry<Integer, Long>> maxEntry = occurrences.entrySet().stream().max(Map.Entry.comparingByValue());

		System.out.println("Occurrences: " + occurrences);

		if (maxEntry.isPresent()) {
			System.out.println("Element with max occurrences: " + maxEntry.get().getKey());
			System.out.println("Count: " + maxEntry.get().getValue());
		}
		else {
			System.out.println("No elements found");
		}
	}

}
