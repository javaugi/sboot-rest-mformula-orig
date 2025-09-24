/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.interview.cls.recd;

public record Constant(double value) implements Expression {

    @Override
    public double evaluate() {
        return value;
    }
}
