/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.interview.cls.recd;

//Sealed Class with Records
// Sealed class with record subclasses
public sealed interface Expression permits Constant, Plus, Minus, Multiply {

    double evaluate();
}
