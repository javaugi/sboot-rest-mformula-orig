/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.kafkamicroservice;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentEventConsumer {
    
    private final PaymentProcessor paymentProcessor;
    private final PaymentEventProducer paymentEventProducer;
    
    @KafkaListener(topics = "payment-requests")
    @CircuitBreaker(name = "paymentProcessing")
    public void processPaymentRequest(PaymentRequestEvent event) {
        PaymentResult result = paymentProcessor.process(event);
        
        if (result.success()) {
            paymentEventProducer.publishPaymentCompleted(result);
        } else {
            paymentEventProducer.publishPaymentFailed(event, result.error());
        }
    }
}
