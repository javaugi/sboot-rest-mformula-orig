/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.interview.cls.recd;

public record Minus(Expression left, Expression right) implements Expression {

    @Override
    public double evaluate() {
        return left.evaluate() - right.evaluate();
    }
}
