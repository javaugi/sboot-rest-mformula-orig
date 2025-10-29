/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.interview.hrank.func.consumer;

import java.util.function.Consumer;

public class ConsumerExample {

    public static void main(String[] args) {
        // Basic Consumer
        Consumer<String> printConsumer = s -> System.out.println(s);
        printConsumer.accept("Hello Consumer!");

        // Method reference
        Consumer<String> printMethodRef = System.out::println;
        printMethodRef.accept("Method Reference!");

        // Chaining Consumers
        Consumer<String> upperCaseConsumer = s -> System.out.println(s.toUpperCase());
        Consumer<String> lengthConsumer = s -> System.out.println("Length: " + s.length());

        Consumer<String> chainedConsumer = upperCaseConsumer.andThen(lengthConsumer);
        chainedConsumer.accept("hello world");
    }
}
