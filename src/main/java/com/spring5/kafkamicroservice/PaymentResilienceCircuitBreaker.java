    /*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.kafkamicroservice;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

/**
 *
 * @author javaugi
 */
public class PaymentResilienceCircuitBreaker {
    //see PaymentKafkaConfig 
    
    @CircuitBreaker(name = "stripe", fallbackMethod = "stripeFallback")
    public PaymentResult processStripePayment(PaymentRequest request) {
        // Stripe API call
        return null;
    }

    public PaymentResult stripeFallback(PaymentRequest request, Exception ex) {
        // Log, notify, or try alternative provider
        return new PaymentResult(false, "STRIPE_UNAVAILABLE");
    }    
}
