/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.kafkamicroservice;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class AdyenPaymentController {

    private final @Qualifier("paymentReqKafkaTemplate")
    KafkaTemplate<String, AdyenPaymentRequest> kafkaTemplate;
    private final AdyenPaymentConsumerService adyenPaymentService;
    private final AdyenPaymentStatusService paymentStatusService;

    @PostMapping("/initiate")
    public ResponseEntity<AdyenPaymentResponse> initiatePayment(
            @RequestBody AdyenPaymentRequest request) {
        // Immediate validation
        if (!request.validate()) {
            return ResponseEntity.badRequest().build();
        }

        // For immediate payment methods (like cards)
        if (request.isSynchronous()) {
            try {
                AdyenPaymentResponse response = adyenPaymentService.makeAdyenPayment(request);
                return ResponseEntity.ok(AdyenPaymentResponse.fromAdyenResponse(response));
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
            }
        } // For asynchronous methods (like PayPal)
        else {
            kafkaTemplate.send("payment.requests" + request.getPaymentId(), request);
            return ResponseEntity.accepted().body(AdyenPaymentResponse.pending(request.getPaymentId()));
        }
    }

    @GetMapping("/status/{paymentId}")
    public ResponseEntity<AdyenPaymentStatus> getPaymentStatus(@PathVariable String paymentId) {
        // Query DB or Adyen API for status
        return ResponseEntity.ok(paymentStatusService.getStatus(paymentId));
    }
}
