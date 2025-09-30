/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.pharmacy;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;

@Service
public class CheckCoverageCircuitBreaker {
    // Circuit Breaker:

    @CircuitBreaker(name = "insuranceService", fallbackMethod = "checkCoverageFallback")
    public InsuranceResponse checkCoverage(String medication, String insuranceId) {
        // REST call implementation
        return new InsuranceResponse("CircuitBreaker", null);
    }

    public InsuranceResponse checkCoverageFallback(
            String medication, String insuranceId, Exception ex) {
        return new InsuranceResponse("SERVICE_UNAVAILABLE", null);
    }
}
