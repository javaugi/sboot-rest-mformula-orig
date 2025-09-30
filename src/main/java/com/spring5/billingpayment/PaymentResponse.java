/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.billingpayment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {

    private String status; // e.g., "requires_action", "succeeded", "failed", "pending"
    private String message; // User-friendly message
    private String paymentIntentId; // Stripe's PaymentIntent ID
    private String clientSecret; // Used by frontend for 3D Secure or other confirmations
    private String orderId; // Your internal order ID
}
