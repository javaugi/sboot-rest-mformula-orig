/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.kafkamicroservice;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdyenPaymentRequests {
    private boolean synchronous;
    private String paymentId;
    private String apiKey;
    private String merchantAccount;
    private String environment;
    private String endpoint;
    private String returnUrl;    
    private AdyenPaymentAmount amount;
    private String paymentMethod;
}
