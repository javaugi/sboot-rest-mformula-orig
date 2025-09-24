/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.kafkamicroservice;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Service;
import retrofit2.http.Header;

@Service
@RequiredArgsConstructor
public class PaymentEventDrivenSourcing {
    private static final Logger log = LoggerFactory.getLogger(PaymentEventDrivenSourcing.class);
    
    private final PaymentRepository paymentRepository;
    
    @KafkaListener(topics = "payment-requests")
    public void processPaymentRequest(@Header(KafkaHeaders.RECEIVED_KEY) String key, PaymentRequestEvent event) {
        if (paymentRepository.existsById(key)) {
            log.info("Duplicate payment request {}", key);
            return;
        }
        // Process payment
    }
    
}
