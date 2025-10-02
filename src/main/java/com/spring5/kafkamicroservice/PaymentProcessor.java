/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.kafkamicroservice;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentProcessor {

	private static final Logger log = LoggerFactory.getLogger(PaymentProcessor.class);

	private final PaymentStripeClient stripeClient;

	private final PaymentPayPalClient paypalClient;

	@CircuitBreaker(name = "stripePayment", fallbackMethod = "processWithFallback")
	public PaymentResult process(PaymentRequestEvent event) {
		if (event.method().getCard() != null) {
			return stripeClient.charge(event);
		}
		else if (event.method().getPaypal() != null) {
			return paypalClient.createOrder(event);
		}
		else {
			throw new UnsupportedOperationException("Unsupported payment method");
		}

		/*
		 * return switch (event.method()) { case CREDIT_CARD ->
		 * stripeClient.charge(event); case PAYPAL -> paypalClient.createOrder(event);
		 * default -> throw new
		 * UnsupportedOperationException("Unsupported payment method"); }; //
		 */
	}

	public PaymentResult processWithFallback(PaymentRequestEvent event, Exception ex) {
		log.error("Payment failed, trying fallback provider", ex);
		// Implement fallback logic (e.g., try different provider)
		return paypalClient.createOrder(event);
	}

}
