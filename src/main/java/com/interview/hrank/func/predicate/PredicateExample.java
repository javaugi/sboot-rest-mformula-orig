/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.interview.hrank.func.predicate;

import java.util.function.Predicate;

public class PredicateExample {

    public static void main(String[] args) {
        // Basic Predicates
        Predicate<String> lengthPredicate = s -> s.length() > 3;
        Predicate<String> startsWithJPredicate = s -> s.startsWith("J");

        System.out.println(lengthPredicate.test("Java")); // true
        System.out.println(startsWithJPredicate.test("Hello")); // false

        // Predicate chaining
        Predicate<String> complexPredicate = lengthPredicate.and(startsWithJPredicate);
        System.out.println(complexPredicate.test("Java")); // true
        System.out.println(complexPredicate.test("Hi")); // false

        // Negate
        Predicate<String> notStartsWithJ = startsWithJPredicate.negate();
        System.out.println(notStartsWithJ.test("Hello")); // true
    }
}
