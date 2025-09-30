/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.kafkamicroservice;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdyenPaymentConsumerService {

    private static final Logger log = LoggerFactory.getLogger(AdyenPaymentConsumerService.class);

    private final AdyenClient adyenClient;
    private final AdyenConfig adyenConfig;
    private final AdyenPaymentEventProducer paymentEventProducer;
    private final AdyenPaymentRepository paymentRepository;

    @KafkaListener(topics = "payment.requests")
    @CircuitBreaker(name = "adyenPayment", fallbackMethod = "processPaymentFallback")
    @Retry(name = "adyenRetry")
    public void processPayment(AdyenPaymentRequest request) {
        AdyenPaymentResponse response = makeAdyenPayment(request);

        if (response.isAuthorised()) {
            paymentEventProducer.publishPaymentSuccess(request.getPaymentId(), response);
        } else {
            paymentEventProducer.publishPaymentFailure(
                    request.getPaymentId(), response.getRefusalReason());
        }
    }

    @KafkaListener(topics = "payments")
    @Transactional
    public void processPayment(
            @Header(KafkaHeaders.RECEIVED_KEY) String key, AdyenPaymentRequest request) {

        if (paymentRepository.existsByIdempotencyKey(key)) {
            return; // Deduplication
        }

        AdyenPayment payment = processWithAdyen(request);
        payment.setIdempotencyKey(key);
        paymentRepository.save(payment); // Atomic transaction
    }

    private AdyenPayment processWithAdyen(AdyenPaymentRequest request) {
        return null;
    }

    @Retry(name = "adyenRetry")
    public AdyenPaymentResponse makeAdyenPayment(AdyenPaymentRequest request) {
        AdyenPayments payments = new AdyenPayments(adyenClient);

        AdyenPaymentAmount amount
                = AdyenPaymentAmount.builder()
                        .currency(request.getCurrency())
                        .value(request.getAmount().multiply(BigDecimal.valueOf(100)).longValue())
                        .paymentMethod(request.getPaymentMethod().toString())
                        .reference(request.getReference())
                        .returnUrl(request.getReturnUrl())
                        .build();

        AdyenPaymentRequests adyenRequests
                = AdyenPaymentRequests.builder()
                        .merchantAccount(adyenConfig.getMerchantAccount())
                        .amount(amount)
                        .build();

        return payments.payments(adyenRequests);
    }

    public void processPaymentFallback(AdyenPaymentRequest request, Exception ex) {
        log.error("Payment failed after retries: {}", request.getPaymentId(), ex);
        paymentEventProducer.publishPaymentFailure(
                request.getPaymentId(), "Payment service unavailable");
    }

    public boolean validate(AdyenPaymentRequest request) {
        return true;
    }
}
