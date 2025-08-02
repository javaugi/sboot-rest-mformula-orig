/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.kafkamicroservice;

import org.springframework.stereotype.Service;

@Service
public class AdyenPaymentStatusService {
    public AdyenPaymentStatus getStatus(String paymentId) {
        return AdyenPaymentStatus.COMPLETED;
    }
}
