/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.interview.cls.iface;

// Sealed Interface Example
// Sealed interface
public sealed interface PaymentMethod permits CreditCard, PayPal, BankTransfer {

	void processPayment(double amount);

}
