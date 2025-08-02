/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.pharmacy;

import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

@Service
public class CheckCoverageRetryLogic {
    @Retryable(value = {ResourceAccessException.class}, 
               maxAttempts = 3, 
               backoff = @Backoff(delay = 1000))
    public InsuranceResponse checkCoverage(String medication, String insuranceId) {
        // REST call implementation
        return new InsuranceResponse("Retry", null);
    }    
}
