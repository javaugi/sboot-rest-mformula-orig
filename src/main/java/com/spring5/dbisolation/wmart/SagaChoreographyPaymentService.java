/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.wmart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
// @KafkaListener(topics = "order-events", groupId = "payment-service")
public class SagaChoreographyPaymentService {

	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;

	@KafkaListener(topics = "order-events", groupId = "payment-service")
	public void onMessage(String message) {
		if (message.startsWith("OrderCreated:")) {
			String orderId = message.split(":")[1];
			// 1. Process payment
			boolean paymentSuccess = processPayment(orderId); // Simulate payment
																// processing

			// 2. Publish payment status event
			if (paymentSuccess) {
				kafkaTemplate.send("payment-events", "PaymentSuccess:" + orderId);
			}
			else {
				kafkaTemplate.send("payment-events", "PaymentFailed:" + orderId);
			}
		}
	}

	private boolean processPayment(String orderId) {
		// Simulate payment logic (e.g., call a payment gateway)
		return Math.random() > 0.2; // 80% success rate for demonstration
	}

}
