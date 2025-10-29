/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.interview.hrank.func.supplier;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SupplierStreamExample {

    public static void main(String[] args) {
        // Generate infinite stream with Supplier
        Supplier<Double> randomSupplier = Math::random;

        System.out.println("10 random numbers:");
        Stream.generate(randomSupplier)
                .limit(10)
                .forEach(System.out::println);

        // Supplier for objects
        Supplier<Person> personSupplier = () -> {
            String[] names = {"John", "Jane", "Jack", "Alice", "Bob"};
            Random random = new Random();
            return new Person(names[random.nextInt(names.length)],
                    random.nextInt(50) + 20);
        };

        System.out.println("\nGenerated people:");
        Stream.generate(personSupplier)
                .limit(5)
                .forEach(p -> System.out.println(p.getName() + " - " + p.getAge()));

        // Reusing streams with Supplier
        Supplier<Stream<String>> streamSupplier = ()
                -> Stream.of("Java", "Python", "C++", "JavaScript");

        // Can reuse the same stream multiple times
        long count = streamSupplier.get().count();
        System.out.println("\nCount: " + count);

        List<String> filtered = streamSupplier.get()
                .filter(s -> s.startsWith("J"))
                .collect(Collectors.toList());
        System.out.println("Filtered: " + filtered);
    }

    static class Person {

        private String name;
        private int age;

        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }
    }
}
