/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.hackerrank.cvshealth;

// 3. Concrete Strategy 2: PayPal Payment
public class CvsPayPalPayment implements CvsPaymentStrategy {

    private String email;

    public CvsPayPalPayment(String email) {
        this.email = email;
    }

    @Override
    public void processPayment(double amount) {
        // Simulate PayPal API calls and authentication
        System.out.println(
                "Processing PayPal payment of $" + amount + " for account: " + email + "...");
        // Add actual PayPal API integration here
        System.out.println("PayPal payment successful!");
    }
}
