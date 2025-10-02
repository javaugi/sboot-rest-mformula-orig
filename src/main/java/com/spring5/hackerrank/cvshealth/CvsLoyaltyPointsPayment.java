/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.hackerrank.cvshealth;

// 4. Concrete Strategy 3: CVS Loyalty Points Redemption
public class CvsLoyaltyPointsPayment implements CvsPaymentStrategy {

	private String loyaltyCardNumber;

	public CvsLoyaltyPointsPayment(String loyaltyCardNumber) {
		this.loyaltyCardNumber = loyaltyCardNumber;
	}

	@Override
	public void processPayment(double amount) {
		// Simulate checking loyalty points balance and redemption logic
		System.out.println(
				"Processing CVS Loyalty Points redemption of $" + amount + " for card: " + loyaltyCardNumber + "...");
		// Add actual loyalty system integration here
		System.out.println("CVS Loyalty Points payment successful!");
	}

}
