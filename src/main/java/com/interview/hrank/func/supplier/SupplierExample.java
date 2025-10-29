/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.interview.hrank.func.supplier;

import java.util.*;
import java.util.function.Supplier;

public class SupplierExample {

    public static void main(String[] args) {
        // Basic Suppliers
        Supplier<String> stringSupplier = () -> "Hello from Supplier!";
        Supplier<Double> randomSupplier = () -> Math.random();
        Supplier<Date> dateSupplier = () -> new Date();

        System.out.println(stringSupplier.get());
        System.out.println(randomSupplier.get());
        System.out.println(dateSupplier.get());

        // Object supplier
        Supplier<List<String>> listSupplier = () -> new ArrayList<>();
        List<String> newList = listSupplier.get();
        newList.add("First item");
        System.out.println("New list: " + newList);
    }
}
