/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.kafkamicroservice;

import com.adyen.model.checkout.PaymentMethod;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class AdyenPaymentRequest {

    private boolean synchronous;
    private String paymentId;
    private String apiKey;
    private String merchantAccount;
    private String environment;
    private String endpoint;
    private String returnUrl;
    private String currency;
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
    private String reference;

    public boolean validate() {
        return true;
    }
}
