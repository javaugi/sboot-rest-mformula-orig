/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.kafkamicroservice;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class AdyenIdempotencyHandlingReconciliationJob {
    private static final Logger log = LoggerFactory.getLogger(AdyenIdempotencyHandlingReconciliationJob.class);
    
    private final AdyenPaymentRepository paymentRepository;
    private final AdyenClient adyenClient;

    @KafkaListener(topics = "payment.requests")
    public void processPayment(@Payload AdyenPaymentRequest request,
        @Header(KafkaHeaders.RECEIVED_KEY) String paymentId) {

        if (paymentRepository.existsByIdAndStatusNot(paymentId, AdyenPaymentStatus.PENDING)) {
            log.info("Duplicate payment request {}", paymentId);
            return;
        }

        // Process payment
    }

    @Scheduled(cron = "0 0 3 * * ?") // Daily at 3 AM
    @Transactional
    public void reconcilePayments() {
        List<AdyenPayment> pendingPayments = paymentRepository.findByStatus(AdyenPaymentStatus.PENDING);

        pendingPayments.forEach(payment -> {
            try {
                AdyenPaymentResponse response = adyenClient.getPaymentDetails(payment.getPspReference());
                updatePaymentStatus(payment, response);
            } catch (Exception e) {
                log.error("Failed to reconcile payment {}", payment.getId(), e);
            }
        });
    }
    
    private void updatePaymentStatus(AdyenPayment payment, AdyenPaymentResponse response) {
        
    }
}
