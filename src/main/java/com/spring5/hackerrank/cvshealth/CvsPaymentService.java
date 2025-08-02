/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.hackerrank.cvshealth;

import org.springframework.stereotype.Service;

@Service
public class CvsPaymentService {
    private CvsPaymentStrategy paymentStrategy;

    // Setter for dynamic strategy change
    public void setPaymentStrategy(CvsPaymentStrategy paymentStrategy) {
        this.paymentStrategy = paymentStrategy;
    }

    public void checkout(double amount) {
        if (paymentStrategy == null) {
            System.err.println("No payment strategy set. Cannot process payment.");
            return;
        }
        System.out.println("\nInitiating checkout for $" + amount);
        paymentStrategy.processPayment(amount);
        System.out.println("Checkout complete.");
    }

    public static void main(String[] args) {
        CvsPaymentService service = new CvsPaymentService();

        // Pay with Credit Card
        service.setPaymentStrategy(new CvsCreditCardPayment("1234-5678-9012-3456", "12/25", "123"));
        service.checkout(100.00);

        // Pay with PayPal
        service.setPaymentStrategy(new CvsPayPalPayment("user@example.com"));
        service.checkout(50.50);

        // Pay with CVS Loyalty Points
        service.setPaymentStrategy(new CvsLoyaltyPointsPayment("CVS123456789"));
        service.checkout(25.75);

        // Example of adding a new payment method easily (e.g., Apple Pay)
        // Without modifying CvsPaymentService or existing strategies
        // public class ApplePayPayment implements PaymentStrategy { ... }
        // service.setPaymentStrategy(new ApplePayPayment(...));
        // service.checkout(75.00);
    }    
}
