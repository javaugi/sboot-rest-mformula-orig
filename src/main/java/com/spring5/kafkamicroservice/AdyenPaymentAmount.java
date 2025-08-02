/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.kafkamicroservice;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdyenPaymentAmount {
    private String currency;
    private long value;
    private String returnUrl;    
    private BigDecimal amount;
    private String paymentMethod;
    private String reference;
    
}
