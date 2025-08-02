/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.billingpayment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/billing")
public class BillingController {
    /*
Key Features Implemented:

Resilience4j Integration:
    Circuit breaker pattern to prevent cascading failures
    Retry mechanism for transient failures
    Fallback methods for graceful degradation
Proper Error Handling:
    Custom exceptions with HTTP status codes
    Global exception handler
    Standardized error responses
Idempotency:
    PayPal's API handles idempotency keys internally
    Each request generates unique IDs for plans/agreements
Response Standardization:
    Consistent response format for success and failure
    Includes approval URLs for PayPal redirect flow
Configuration:
    Externalized configuration for different environments
    Proper SDK initialization
To use this in production, you would need to:
    Replace the sandbox credentials with live credentials
    Implement proper logging
    Add monitoring for the circuit breaker states
    Implement reconciliation processes for failed payments
    Add proper security measures (like input validation)    
    */

    @Autowired
    private PayPalBillingService payPalBillingService;

    @PostMapping("/create-agreement")
    public ResponseEntity<?> createBillingAgreement(@RequestBody BillingPlanRequest request) {
        try {
            BillingAgreementResponse response = payPalBillingService.createBillingAgreement(request);

            if ("FAILED".equals(response.getStatus())) {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body(response);
            }

            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BillingErrorResponse("Failed to create billing agreement", ex.getMessage()));
        }
    }
}
