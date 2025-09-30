/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.kafkamicroservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {
    // Never log full payment details
    // Security - PCI DSS Compliance:

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    public void process(PaymentRequest request) {
        log.info(
                "Processing payment {} for amount {}",
                maskPaymentId(request.getId()), // Shows only last 4 digits
                request.getAmount());
    }

    private String maskPaymentId(String id) {
        return "****" + id.substring(id.length() - 4);
    }
}
