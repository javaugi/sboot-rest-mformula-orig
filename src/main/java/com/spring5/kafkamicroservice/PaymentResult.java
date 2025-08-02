/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.kafkamicroservice;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentResult {
    private boolean success;
    private String paymentId;
    private String error;
    
    public PaymentResult(boolean success, String error) {
        this.success = success;
        this.error = error;
    }
    
    public boolean success() {
        return success;
    }
    
    public String error() {
        return error;
    }
}
