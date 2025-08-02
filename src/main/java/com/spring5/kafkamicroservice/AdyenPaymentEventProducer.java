/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.kafkamicroservice;

import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdyenPaymentEventProducer {
    
    private final @Qualifier("paymentSuccessKafkaTemplate") KafkaTemplate<String, AdyenPaymentSuccessEvent> successTemplate;
    private final @Qualifier("paymentFailedKafkaTemplate") KafkaTemplate<String, AdyenPaymentFailedEvent> failedTemplate;

    public void publishPaymentSuccess(String paymentId, AdyenPaymentResponse response) {
        AdyenPaymentSuccessEvent event = new AdyenPaymentSuccessEvent(
                paymentId, response.getPspReference(), response.getAmount(), Instant.now());
        successTemplate.send("payment.success", event);
    }

    public void publishPaymentFailure(String paymentId, String reason) {
        AdyenPaymentFailedEvent event = new AdyenPaymentFailedEvent(
                paymentId,
                reason,
                Instant.now()
        );
        failedTemplate.send("payment.failed", event);
    }
}
