/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.interview.cls.sealed.shape;

// Define a sealed class
/*
Key Corrections:
    Added abstract to classes that declare abstract methods but don't implement them
    Interfaces don't need the abstract keyword as all methods are implicitly abstract
    Abstract sealed classes must be explicitly marked as abstract

The rule is:
    If a class has abstract methods, it must be declared abstract
    Sealed classes can be either abstract or concrete
    Interfaces are implicitly abstract, so no need for the keyword
 */
public abstract sealed class Shape permits Circle, Rectangle, Triangle, Square {

	public abstract double area();

	// Common methods or fields for all shapes
	public String getDescription() {
		return "This is a shape.";
	}

}
