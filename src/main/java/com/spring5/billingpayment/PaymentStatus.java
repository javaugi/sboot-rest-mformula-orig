/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.billingpayment;

/**
 *
 * @author javaugi
 */
public enum PaymentStatus {
    REQUIRES_ACTION, // Requires 3D Secure or other customer action
    SUCCEEDED,
    FAILED,
    PENDING, // Initial state, awaiting confirmation via webhook
    CANCELED    
}
