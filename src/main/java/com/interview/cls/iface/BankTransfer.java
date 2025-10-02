/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.interview.cls.iface;

public non-sealed class BankTransfer implements PaymentMethod {

	@Override
	public void processPayment(double amount) {
		System.out.println("Processing bank transfer: $" + amount);
	}

}
