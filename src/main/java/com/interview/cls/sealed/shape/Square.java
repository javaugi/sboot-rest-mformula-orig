/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.interview.cls.sealed.shape;

// Non-sealed permitted subclass (can be extended by any class)
public non-sealed class Square extends Shape {

	private final double width;

	public Square(double width) {
		this.width = width;
	}

	@Override
	public double area() {
		return width * width;
	}

}
