/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.interview;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 *
 * @author javau
 */
public class MapVsMapToObj {

    /*
Key Points:
    LinkedHashMap preserves insertion order, crucial for finding "first" occurrences - HashMap if order is not required
    Collectors.counting() efficiently counts occurrences
    mapToObj() converts int stream to Character objects
    Filter and stream operations enable complex analysis on the frequency map
    Optional handles cases where no matching character is found


Key Advantages of mapToObj() over map():
    Returns Object Stream - creates Stream<Character> or Stream<String> instead of IntStream
    Easier Collection - simpler to collect into lists, maps, or other collections
    Better Type Safety - works with proper object types instead of primitive ints
    More Readable - often results in cleaner, more maintainable code

When to use mapToObj() vs map():
    Use mapToObj() when you want to work with objects (Character, String, custom objects)
    Use map() when you want to perform mathematical operations and keep working with primitives
    Use mapToObj() when collecting into object-based collections
    Use map() for better performance with primitive operations
     */
    public static void main(String[] args) {
        m27CompleteAnalysisUtilityMethod();
    }

    public static void m1ConvertToCharacterObjects() {
        String text = "hello";
        List<Character> charList = text.chars()
            .mapToObj(c -> (char) c)
            .collect(Collectors.toList());
        System.out.println(charList); // [h, e, l, l, o]
    }

    public static void m2ConvertToStringObjectsTrans() {
        String text = "hello";
        List<String> stringList = text.chars()
            .mapToObj(c -> String.valueOf((char) c).toUpperCase())
            .collect(Collectors.toList());
        System.out.println(stringList); // [H, E, L, L, O]
    }

    public static void m3CreateKeyValuePairs() {
        String text = "abc";
        Map<Character, Integer> charAsciiMap = text.chars()
            .mapToObj(c -> (char) c)
            .collect(Collectors.toMap(
                Function.identity(),
                Character::getNumericValue
            ));
        System.out.println(charAsciiMap); // {a=10, b=11, c=12}
    }

    public static void m4GenerateCharFreqMap() {
        String text = "hello world";
        Map<Character, Long> frequencyMap = text.chars()
            .mapToObj(c -> (char) c)
            .collect(Collectors.groupingBy(
                Function.identity(),
                Collectors.counting()
            ));
        System.out.println(frequencyMap);
        // { =1, r=1, d=1, e=1, w=1, h=1, l=3, o=2}
    }

    public static void m5ConvertToHexadecimal() {
        String text = "hello";
        List<String> hexList = text.chars()
            .mapToObj(c -> "0x" + Integer.toHexString(c))
            .collect(Collectors.toList());
        System.out.println(hexList); // [0x68, 0x65, 0x6c, 0x6c, 0x6f]
    }

    public static void m6ConvertToHexadecimal() {
        String text = "hello";
        List<String> hexList = text.chars()
            .mapToObj(c -> "0x" + Integer.toHexString(c))
            .collect(Collectors.toList());
        System.out.println(hexList); // [0x68, 0x65, 0x6c, 0x6c, 0x6f]
    }

    public static void m7FormattedOutput() {
        String text = "Hello123World!";
        List<String> lettersOnly = text.chars()
            .filter(Character::isLetter)
            .mapToObj(c -> String.valueOf((char) c).toLowerCase())
            .collect(Collectors.toList());
        System.out.println(lettersOnly); // [h, e, l, l, o, w, o, r, l, d]
    }

    public static void m8CreatCustomObj() {
        class CharInfo {

            char character;
            int ascii;

            CharInfo(char c, int a) {
                this.character = c;
                this.ascii = a;
            }

            public String toString() {
                return character + ":" + ascii;
            }
        }

        String text = "test";
        List<CharInfo> charInfoList = text.chars()
            .mapToObj(c -> new CharInfo((char) c, c))
            .collect(Collectors.toList());
        System.out.println(charInfoList); // [t:116, e:101, s:115, t:116]
    }

    public static void m9ConvertToBinary() {
        String text = "AB";
        List<String> binaryList = text.chars()
            .mapToObj(c -> Integer.toBinaryString(c))
            .collect(Collectors.toList());
        System.out.println(binaryList); // [1000001, 1000010]
    }

    public static void m10TransWithConditions() {
        String text = "a1b2c3";
        List<String> transformed = text.chars()
            .mapToObj(c -> {
                if (Character.isDigit(c)) {
                    return "DIGIT_" + (char) c;
                } else if (Character.isLetter(c)) {
                    return "LETTER_" + Character.toUpperCase((char) c);
                }
                return "OTHER_" + (char) c;
            })
            .collect(Collectors.toList());
        System.out.println(transformed);
        // [LETTER_A, DIGIT_1, LETTER_B, DIGIT_2, LETTER_C, DIGIT_3]
    }

    public static void m11JoinWithSeparators() {
        String text = "hello";
        String result = text.chars()
            .mapToObj(c -> String.valueOf((char) c))
            .collect(Collectors.joining("-"));
        System.out.println(result); // h-e-l-l-o
    }

    public static void m21FirstNonRepeatChar() {
        String text = "swiss";

        // Find first non-repeating character
        Character firstNonRepeat = text.chars()
            .mapToObj(c -> (char) c)
            .collect(Collectors.groupingBy(
                Function.identity(),
                LinkedHashMap::new, // Preserves insertion order
                Collectors.counting()
            ))
            .entrySet()
            .stream()
            .filter(entry -> entry.getValue() == 1)
            .map(Map.Entry::getKey)
            .findFirst()
            .orElse(null);

        System.out.println("First non-repeating char: " + firstNonRepeat); // w
    }

    public static void m22MostFrequentChar() {
        String text = "programming";

        // Find most frequent character
        Map.Entry<Character, Long> mostFrequent = text.chars()
            .mapToObj(c -> (char) c)
            .collect(Collectors.groupingBy(
                Function.identity(),
                Collectors.counting()
            ))
            .entrySet()
            .stream()
            .max(Map.Entry.comparingByValue())
            .orElse(null);

        System.out.println("Most frequent char: "
            + mostFrequent.getKey() + " (" + mostFrequent.getValue() + " times)"); // m (2 times)
    }

    public static void m23CompleteCharacterAnalysisWLinkedHashMap() {
        String text = "aabbcdeeffg";

        LinkedHashMap<Character, Long> charFrequency = text.chars()
            .mapToObj(c -> (char) c)
            .collect(Collectors.groupingBy(
                Function.identity(),
                LinkedHashMap::new, // Maintains insertion order
                Collectors.counting()
            ));

        System.out.println("Character frequencies (in order): " + charFrequency);
        // {a=2, b=2, c=1, d=1, e=2, f=2, g=1}

        // Find all non-repeating characters in order
        List<Character> nonRepeatingChars = charFrequency.entrySet()
            .stream()
            .filter(entry -> entry.getValue() == 1)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());

        System.out.println("Non-repeating chars in order: " + nonRepeatingChars); // [c, d, g]

        // Find first non-repeating character
        Optional<Character> firstNonRepeat = charFrequency.entrySet()
            .stream()
            .filter(entry -> entry.getValue() == 1)
            .map(Map.Entry::getKey)
            .findFirst();

        System.out.println("First non-repeating char: " + firstNonRepeat.orElse(null)); // c
    }

    public static void m24AdvancedAnalysisWithMultiMetrics() {
        String text = "hello world java programming";

        LinkedHashMap<Character, Long> frequencyMap = text.chars()
            .filter(c -> c != ' ') // Ignore spaces
            .mapToObj(c -> (char) c)
            .collect(Collectors.groupingBy(
                Function.identity(),
                LinkedHashMap::new,
                Collectors.counting()
            ));

        // Multiple analyses
        System.out.println("Complete frequency map: " + frequencyMap);

        // Most frequent character
        Optional<Map.Entry<Character, Long>> maxEntry = frequencyMap.entrySet()
            .stream()
            .max(Map.Entry.comparingByValue());

        maxEntry.ifPresent(entry
            -> System.out.println("Most frequent: '" + entry.getKey() + "' (" + entry.getValue() + " times)")
        );

        // Least frequent character
        Optional<Map.Entry<Character, Long>> minEntry = frequencyMap.entrySet()
            .stream()
            .min(Map.Entry.comparingByValue());

        minEntry.ifPresent(entry
            -> System.out.println("Least frequent: '" + entry.getKey() + "' (" + entry.getValue() + " times)")
        );

        // All non-repeating characters
        List<Character> uniqueChars = frequencyMap.entrySet()
            .stream()
            .filter(entry -> entry.getValue() == 1)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());

        System.out.println("Unique characters: " + uniqueChars);

        // Characters that repeat more than once
        List<Character> repeatingChars = frequencyMap.entrySet()
            .stream()
            .filter(entry -> entry.getValue() > 1)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());

        System.out.println("Repeating characters: " + repeatingChars);
    }

    public static void m25CaseInsensitiveAnalysis() {
        String text = "Hello World";

        LinkedHashMap<Character, Long> caseInsensitiveMap = text.chars()
            .filter(c -> c != ' ')
            .mapToObj(c -> Character.toLowerCase((char) c))
            .collect(Collectors.groupingBy(
                Function.identity(),
                LinkedHashMap::new,
                Collectors.counting()
            ));

        System.out.println("Case-insensitive frequencies: " + caseInsensitiveMap);
        // {h=1, e=1, l=3, o=2, w=1, r=1, d=1}

        // Find first non-repeating (case-insensitive)
        Optional<Character> firstNonRepeat = caseInsensitiveMap.entrySet()
            .stream()
            .filter(entry -> entry.getValue() == 1)
            .map(Map.Entry::getKey)
            .findFirst();

        System.out.println("First non-repeating (case-insensitive): " + firstNonRepeat.orElse(null)); // h
    }

    public static void m26CharacterAnalysisWithCustomObj() {
        class CharStats {

            char character;
            long frequency;
            int firstPosition;

            CharStats(char character, long frequency, int firstPosition) {
                this.character = character;
                this.frequency = frequency;
                this.firstPosition = firstPosition;
            }

            @Override
            public String toString() {
                return character + ":" + frequency + "@" + firstPosition;
            }
        }

        String text = "abracadabra";

        // Create map with character and its first occurrence position
        LinkedHashMap<Character, CharStats> detailedStats = new LinkedHashMap<>();

        text.chars()
            .mapToObj(c -> (char) c)
            .forEach(c -> {
                detailedStats.compute(c, (key, existing) -> {
                    if (existing == null) {
                        return new CharStats(c, 1, text.indexOf(c));
                    }
                    return new CharStats(c, existing.frequency + 1, existing.firstPosition);
                });
            });

        System.out.println("Detailed character statistics:");
        detailedStats.forEach((k, v) -> System.out.println(v));

        // Find first non-repeating with position info
        Optional<CharStats> firstUnique = detailedStats.values()
            .stream()
            .filter(stats -> stats.frequency == 1)
            .min(Comparator.comparingInt(stats -> stats.firstPosition));

        firstUnique.ifPresent(stats
            -> System.out.println("First unique char: " + stats.character + " at position " + stats.firstPosition)
        );
    }

    public static void m27CompleteAnalysisUtilityMethod() {
        String text = "aabbcdeeffg";

        System.out.println("First non-repeating: " + findFirstNonRepeating(text).orElse(null));
        System.out.println("Most frequent: " + findMostFrequent(text).orElse(null));
        System.out.println("Full frequency map: " + getCharacterFrequency(text, true));

        getCharacterFrequencyIgnoreCase(text, true)
            .forEach((k, v) -> System.out.println("key=" + k + ", value=" + v));
        getCharacterFrequencyIgnoreCaseOrderByValue(text, true)
            .entrySet()
            .stream()
            .sorted(Map.Entry.comparingByValue()).forEach(e -> {
            System.out.println("sorted by value key=" + e.getKey() + ", value=" + e.getValue());
        });
        //.forEach((k, v) -> System.out.println("key=" + k + ", value=" + v));
    }

    public static Map<Character, Long> getCharacterFrequency(String text, boolean preserveOrder) {
        return text.chars()
            .mapToObj(c -> (char) c)
            .collect(Collectors.groupingBy(
                Function.identity(),
                preserveOrder ? LinkedHashMap::new : HashMap::new,
                Collectors.counting()
            ));
    }

    public static Map<Character, Long> getCharacterFrequencyIgnoreCase(String text, boolean preserveOrder) {
        return text.toLowerCase().chars()
            .mapToObj(c -> (char) c)
            .collect(Collectors.groupingBy(
                Function.identity(),
                preserveOrder ? LinkedHashMap::new : HashMap::new,
                Collectors.counting()
            ));
    }

    public static Map<Character, Long> getCharacterFrequencyIgnoreCaseOrderByValue(String text, boolean preserveOrder) {
        return text.toLowerCase().chars()
            .mapToObj(c -> (char) c)
            .collect(Collectors.groupingBy(
                Function.identity(),
                preserveOrder ? LinkedHashMap::new : HashMap::new,
                Collectors.counting()
            ));
    }

    public static Optional<Character> findFirstNonRepeating(String text) {
        return getCharacterFrequency(text, true)
            .entrySet()
            .stream()
            .filter(entry -> entry.getValue() == 1)
            .map(Map.Entry::getKey)
            .findFirst();
    }

    public static Optional<Character> findMostFrequent(String text) {
        return getCharacterFrequency(text, false)
            .entrySet()
            .stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey);
    }
}
