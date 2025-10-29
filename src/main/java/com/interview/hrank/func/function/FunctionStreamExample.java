/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.interview.hrank.func.function;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FunctionStreamExample {

    public static void main(String[] args) {
        List<String> names = Arrays.asList("john", "jane", "jack", "alice");

        // Map with Function
        Function<String, String> capitalize
                = s -> s.substring(0, 1).toUpperCase() + s.substring(1);

        List<String> capitalizedNames = names.stream()
                .map(capitalize)
                .collect(Collectors.toList());
        System.out.println("Capitalized: " + capitalizedNames);

        // Multiple transformations
        Function<String, Integer> nameToLength = String::length;
        Function<String, String> addTitle = name -> "Mr. " + name;

        List<String> titledNames = names.stream()
                .map(capitalize.andThen(addTitle))
                .collect(Collectors.toList());
        System.out.println("Titled: " + titledNames);

        // Complex example with objects
        List<Person> people = Arrays.asList(
                new Person("John", 25),
                new Person("Jane", 30),
                new Person("Jack", 35)
        );

        Function<Person, String> getName = Person::getName;
        Function<Person, Integer> getAge = Person::getAge;
        Function<Person, String> personToString
                = p -> p.getName() + " (" + p.getAge() + ")";

        List<String> personStrings = people.stream()
                .map(personToString)
                .collect(Collectors.toList());
        System.out.println("People: " + personStrings);

        // Using Function.identity()
        Map<String, Person> personMap = people.stream()
                .collect(Collectors.toMap(Person::getName, Function.identity()));
        System.out.println("Person Map: " + personMap);
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

        @Override
        public String toString() {
            return name + ":" + age;
        }
    }
}
