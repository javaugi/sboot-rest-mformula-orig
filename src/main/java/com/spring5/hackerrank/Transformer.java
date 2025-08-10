/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.hackerrank;

@FunctionalInterface
interface Transformer<T> {

    T transform(T input); // Single abstract method

    default Transformer<T> andThen(Transformer<T> other) {
        return input -> other.transform(this.transform(input));
    }

    static <T> Transformer<T> identity() {
        return input -> input;
    }
}
