/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.interview.cls.veh;

public sealed class Car extends Vehicle permits Sedan, SUV, Coupe {

	public Car(String manufacturer) {
		super(manufacturer);
	}

	@Override
	public void startEngine() {
		System.out.println("Car engine started");
	}

}
