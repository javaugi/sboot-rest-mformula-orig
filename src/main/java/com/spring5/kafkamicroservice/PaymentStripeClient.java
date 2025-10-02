/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.kafkamicroservice;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentStripeClient {

	private final StripeProperties properties;

	public PaymentResult charge(PaymentRequestEvent event) {
		Stripe.apiKey = properties.getApiKey();

		PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
			.setAmount(event.amount().longValue() * 100) // in cents
			.setCurrency(event.currency())
			.putMetadata("orderId", event.orderId())
			.build();

		try {
			PaymentIntent intent = PaymentIntent.create(params);
			return new PaymentResult(true, intent.getId(), "Payment succeeded");
		}
		catch (StripeException e) {
			return new PaymentResult(false, null, e.getMessage());
		}
	}

}
