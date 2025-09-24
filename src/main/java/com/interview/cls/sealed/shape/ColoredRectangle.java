/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.interview.cls.sealed.shape;

// Permitted subclass of Rectangle
public final class ColoredRectangle extends Rectangle {

    public String color;

    public ColoredRectangle(double length, double width, String color) {
        super(length, width);
        this.color = color;
    }
}
