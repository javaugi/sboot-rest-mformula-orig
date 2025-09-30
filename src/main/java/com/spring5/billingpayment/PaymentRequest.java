/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.billingpayment;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
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
public class PaymentRequest {

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount; // Amount in major currency units (e.g., USD)

    @NotBlank(message = "Currency is required")
    private String currency; // e.g., "usd", "eur"

    @NotBlank(message = "Payment Method ID is required")
    private String paymentMethodId; // Obtained from Stripe.js/Elements on the frontend

    @NotBlank(message = "Order ID is required")
    private String orderId; // Your internal order ID for idempotency and tracking

    private String customerEmail; // Optional: Customer email for receipt
}
