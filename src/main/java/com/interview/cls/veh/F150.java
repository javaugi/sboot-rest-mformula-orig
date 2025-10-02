/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.interview.cls.veh;

/**
 * @author javau
 */
public class F150 extends Truck {

	public F150(String manufacturer) {
		super(manufacturer);
	}

	@Override
	public void startEngine() {
		System.out.println("Truck engine started");
	}

}
