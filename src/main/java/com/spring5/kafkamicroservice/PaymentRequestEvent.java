/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.kafkamicroservice;

import com.stripe.model.PaymentMethod;
import java.math.BigDecimal;
import java.util.Map;

public record PaymentRequestEvent(
        String paymentId,
        String orderId,
        BigDecimal amount,
        String currency,
        PaymentMethod method, // CREDIT_CARD, PAYPAL, etc.
        Map<String, String> metadata) {

}
