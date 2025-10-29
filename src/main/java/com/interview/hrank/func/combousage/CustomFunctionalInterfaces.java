/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.interview.hrank.func.combousage;

import java.util.*;
import java.util.function.*;

public class CustomFunctionalInterfaces {

    // Custom functional interface
    @FunctionalInterface
    interface TriFunction<T, U, V, R> {

        R apply(T t, U u, V v);
    }

    @FunctionalInterface
    interface ThrowingConsumer<T, E extends Exception> {

        void accept(T t) throws E;
    }

    public static void main(String[] args) {
        // Using custom TriFunction
        TriFunction<Integer, Integer, Integer, Integer> sumThree
                = (a, b, c) -> a + b + c;
        System.out.println("Sum of three: " + sumThree.apply(10, 20, 30));

        TriFunction<String, String, String, String> concatThree
                = (s1, s2, s3) -> s1 + " " + s2 + " " + s3;
        System.out.println("Concatenated: " + concatThree.apply("Hello", "from", "Java"));

        // Safe consumer for exception handling
        List<String> numbers = Arrays.asList("1", "2", "abc", "4");

        numbers.forEach(safeConsumer(s -> {
            int num = Integer.parseInt(s);
            System.out.println("Number: " + num);
        }));
    }

    // Helper method for exception handling in consumers
    public static <T> Consumer<T> safeConsumer(ThrowingConsumer<T, Exception> throwingConsumer) {
        return t -> {
            try {
                throwingConsumer.accept(t);
            } catch (Exception e) {
                System.err.println("Error processing: " + t + " - " + e.getMessage());
            }
        };
    }
}
