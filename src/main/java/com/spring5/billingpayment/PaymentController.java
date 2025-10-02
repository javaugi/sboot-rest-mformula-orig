/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.billingpayment;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

	private final StripePaymentService stripePaymentService;

	public PaymentController(StripePaymentService stripePaymentService) {
		this.stripePaymentService = stripePaymentService;
	}

	@PostMapping("/charge")
	public ResponseEntity<PaymentResponse> charge(@Valid @RequestBody PaymentRequest request) {
		System.out.println("Received payment request for order: " + request.getOrderId());
		PaymentResponse response = stripePaymentService.createAndConfirmPayment(request);

		// Based on the response status, return appropriate HTTP status codes
		if (PaymentStatus.SUCCEEDED.name().equals(response.getStatus())) {
			return ResponseEntity.ok(response);
		}
		else if (PaymentStatus.REQUIRES_ACTION.name().equals(response.getStatus())) {
			// Frontend needs to use client_secret to complete 3D Secure or other actions
			return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
		}
		else if (PaymentStatus.PENDING.name().equals(response.getStatus())) {
			// Payment is processing, final status via webhook
			return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
		}
		else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response); // Or 402
																					// Payment
																					// Required
		}
	}

	// You might add an endpoint for querying order status manually, but webhooks are
	// preferred.
	// @GetMapping("/status/{orderId}")
	// public ResponseEntity<PaymentResponse> getPaymentStatus(@PathVariable String
	// orderId) {
	// // Implement logic to retrieve status from your database
	// // You can also retrieve from Stripe if needed, but rely on your DB mostly
	// }

}
