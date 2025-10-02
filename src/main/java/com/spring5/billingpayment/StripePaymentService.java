/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.billingpayment;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.net.RequestOptions;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StripePaymentService {

	@Value("${stripe.api.secret-key}")
	private String stripeSecretKey;

	// A simple map to simulate storing order status for idempotency.
	// In a real application, you'd use a database.
	private final Map<String, PaymentStatus> orderStatusStore = new ConcurrentHashMap<>();

	@PostConstruct
	public void init() {
		Stripe.apiKey = stripeSecretKey;
	}

	/**
	 * Initiates a payment by creating and confirming a Stripe PaymentIntent. This method
	 * handles the synchronous part of the payment flow.
	 * @param request The payment request DTO from the client.
	 * @return A PaymentResponse indicating the initial status and necessary details for
	 * the frontend.
	 */
	public PaymentResponse createAndConfirmPayment(PaymentRequest request) {
		// Stripe requires amount in cents (or smallest currency unit)
		long amountInCents = request.getAmount().multiply(new BigDecimal("100")).longValue();

		// 1. Check for Idempotency (if this orderId has been processed before)
		// In a real system, this would involve checking your order database.
		// If the order is already SUCCEEDED or PENDING, return its status.
		if (orderStatusStore.containsKey(request.getOrderId())
				&& (orderStatusStore.get(request.getOrderId()) == PaymentStatus.SUCCEEDED
						|| orderStatusStore.get(request.getOrderId()) == PaymentStatus.PENDING)) {
			// Return existing status if already processing or succeeded
			return new PaymentResponse(orderStatusStore.get(request.getOrderId()).name(),
					"Payment for this order is already being processed or has succeeded.", null, // PaymentIntentId
																									// not
																									// strictly
																									// needed
																									// for
																									// this
																									// idempotent
																									// response
					null, request.getOrderId());
		}

		// --- Create PaymentIntent ---
		PaymentIntentCreateParams createParams = PaymentIntentCreateParams.builder()
			.setAmount(amountInCents)
			.setCurrency(request.getCurrency())
			.setPaymentMethod(request.getPaymentMethodId())
			.setConfirmationMethod(PaymentIntentCreateParams.ConfirmationMethod.AUTOMATIC) // Recommended
			.setConfirm(true) // Automatically confirm the intent after creation
			.setOffSession(false) // Assuming customer is present
			.addPaymentMethodType("card")
			.putMetadata("order_id", request.getOrderId()) // Add your internal order ID
															// as metadata
			.build();

		// Use Idempotency Key to prevent duplicate requests
		// A unique key for each API call, often the orderId or a UUID associated with the
		// attempt
		// For PaymentIntent creation, the orderId is a good candidate for idempotency.
		RequestOptions requestOptions = RequestOptions.builder()
			.setIdempotencyKey(request.getOrderId() + "-" + System.currentTimeMillis()) // Ensure
																						// unique
																						// per
																						// attempt
			.build();

		try {
			PaymentIntent paymentIntent = PaymentIntent.create(createParams, requestOptions);

			// Store initial status (e.g., PENDING or REQUIRES_ACTION) in your database
			orderStatusStore.put(request.getOrderId(), PaymentStatus.PENDING); // Assume
																				// pending
																				// until
																				// webhook
																				// confirms

			return mapStripePaymentIntentToResponse(paymentIntent, request.getOrderId());

		}
		catch (StripeException e) {
			System.err.println("Stripe API Error during PaymentIntent creation: " + e.getMessage());
			e.printStackTrace();
			// Handle specific Stripe errors and map to internal error codes
			String errorMessage = "Payment failed: " + e.getMessage();
			PaymentStatus status = PaymentStatus.FAILED;

			// Example of mapping Stripe error codes
			switch (e.getCode()) {
				case "card_declined":
					errorMessage = "Your card was declined.";
					break;
				case "incorrect_cvc":
					errorMessage = "The CVC provided was incorrect.";
					break;
				case "expired_card":
					errorMessage = "Your card has expired.";
					break;
				case "invalid_charge_amount":
					errorMessage = "The charge amount is invalid.";
					break;
				// Add more cases for specific Stripe error codes (e.g.,
				// authentication_required)
				default:
					errorMessage = "Payment processing error. Please try again or contact support.";
					break;
			}
			orderStatusStore.put(request.getOrderId(), status); // Update status to FAILED
																// in your store
			return new PaymentResponse(status.name(), errorMessage, null, null, request.getOrderId());
		}
		catch (Exception e) {
			System.err.println("Unexpected error during payment processing: " + e.getMessage());
			e.printStackTrace();
			orderStatusStore.put(request.getOrderId(), PaymentStatus.FAILED);
			return new PaymentResponse(PaymentStatus.FAILED.name(), "An unexpected error occurred.", null, null,
					request.getOrderId());
		}
	}

	/**
	 * Maps a Stripe PaymentIntent object to our internal PaymentResponse DTO. This is
	 * where you translate Stripe's various statuses into your system's statuses.
	 */
	private PaymentResponse mapStripePaymentIntentToResponse(PaymentIntent paymentIntent, String orderId) {
		String status = paymentIntent.getStatus();
		PaymentStatus internalStatus;
		String message;
		String clientSecret = paymentIntent.getClientSecret();

		switch (status) {
			case "requires_payment_method":
				internalStatus = PaymentStatus.FAILED; // Payment method was
														// invalid/rejected
				message = "Your payment method is invalid or requires additional information.";
				clientSecret = null; // No client secret needed here
				break;
			case "requires_confirmation":
				// This state usually means the PaymentIntent was created but not
				// confirmed.
				// With automatic confirmation, this should be less common.
				internalStatus = PaymentStatus.REQUIRES_ACTION; // Or pending, depending
																// on flow
				message = "Payment requires confirmation.";
				break;
			case "requires_action":
				// e.g., 3D Secure authentication needed by the customer
				internalStatus = PaymentStatus.REQUIRES_ACTION;
				message = "Payment requires customer action (e.g., 3D Secure).";
				break;
			case "processing":
				internalStatus = PaymentStatus.PENDING; // Still processing, final status
														// via webhook
				message = "Payment is processing. Please check status later.";
				break;
			case "succeeded":
				internalStatus = PaymentStatus.SUCCEEDED;
				message = "Payment succeeded!";
				clientSecret = null; // No client secret needed for a succeeded payment
				break;
			case "canceled":
				internalStatus = PaymentStatus.CANCELED;
				message = "Payment was canceled.";
				clientSecret = null;
				break;
			case "requires_capture":
				internalStatus = PaymentStatus.PENDING; // Authorized but not captured yet
				message = "Payment authorized, awaiting capture.";
				break;
			default:
				internalStatus = PaymentStatus.PENDING; // Unknown or temporary state
				message = "Payment is in an unknown or pending state.";
				break;
		}

		// In a real application, you would update your order's status in the database
		// here
		// based on `internalStatus` and `paymentIntent.getId()`
		return new PaymentResponse(internalStatus.name(), message, paymentIntent.getId(), clientSecret, orderId);
	}

	/**
	 * Simulate updating order status based on webhook. In a real application, this would
	 * update your database.
	 * @param orderId
	 * @param status
	 */
	public void updateOrderStatus(String orderId, PaymentStatus status) {
		orderStatusStore.put(orderId, status);
		System.out.println("Order " + orderId + " status updated to: " + status.name() + " by webhook.");
	}

	// --- Handling Retries ---
	// Retries for synchronous API calls (like createAndConfirmPayment) are generally
	// handled by the client-side (e.g., frontend retries if a network error occurs).
	// The idempotency key (request.getOrderId() in this example) is crucial here.
	//
	// For asynchronous retries (e.g., retrying a webhook processing failure),
	// you'd typically use a message queue (Kafka, RabbitMQ) with dead-letter queues
	// or a dedicated retry mechanism built into your webhook consumer.
	//
	// For manual retrieval: If your webhook fails or is missed, you might
	// have an admin tool or scheduled job that can query Stripe for the
	// status of a specific PaymentIntent ID using
	// PaymentIntent.retrieve(paymentIntentId);

}
