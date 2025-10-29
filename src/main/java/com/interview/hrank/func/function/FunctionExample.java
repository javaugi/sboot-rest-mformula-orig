/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.interview.hrank.func.function;
import java.util.function.Function;

public class FunctionExample {

    public static void main(String[] args) {
        // Basic Functions
        Function<String, Integer> lengthFunction = s -> s.length();
        Function<String, String> upperCaseFunction = s -> s.toUpperCase();

        System.out.println(lengthFunction.apply("Hello")); // 5
        System.out.println(upperCaseFunction.apply("hello")); // HELLO

        // Function chaining
        Function<String, String> processString = upperCaseFunction.andThen(s -> "*** " + s + " ***");
        System.out.println(processString.apply("hello")); // *** HELLO ***

        // Compose
        Function<Integer, Integer> multiplyBy2 = x -> x * 2;
        Function<Integer, Integer> add3 = x -> x + 3;

        Function<Integer, Integer> composed = multiplyBy2.compose(add3); // First add3, then multiplyBy2
        System.out.println(composed.apply(5)); // (5 + 3) * 2 = 16

        Function<Integer, Integer> andThen = multiplyBy2.andThen(add3); // First multiplyBy2, then add3
        System.out.println(andThen.apply(5)); // (5 * 2) + 3 = 13
    }
}
