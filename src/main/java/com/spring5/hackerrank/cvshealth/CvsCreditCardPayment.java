/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.hackerrank.cvshealth;

// 2. Concrete Strategy 1: Credit Card Payment
import org.springframework.stereotype.Service;

@Service
public class CvsCreditCardPayment implements CvsPaymentStrategy {

	private String cardNumber;

	private String expiryDate;

	private String cvv;

	public CvsCreditCardPayment(String cardNumber, String expiryDate, String cvv) {
		this.cardNumber = cardNumber;
		this.expiryDate = expiryDate;
		this.cvv = cvv;
	}

	@Override
	public void processPayment(double amount) {
		// Simulate complex credit card processing logic, API calls, validation etc.
		System.out.println("Processing credit card payment of $" + amount + " using card number: "
				+ cardNumber.substring(cardNumber.length() - 4) + "...");
		// Add actual payment gateway integration here
		System.out.println("Credit card payment successful!");
	}

}
