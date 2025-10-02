/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.interview.cls.iface;

public final class PayPal implements PaymentMethod {

	@Override
	public void processPayment(double amount) {
		System.out.println("Processing PayPal payment: $" + amount);
	}

}
