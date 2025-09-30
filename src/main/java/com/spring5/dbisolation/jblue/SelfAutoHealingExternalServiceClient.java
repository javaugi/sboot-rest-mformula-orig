/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.jblue;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.stereotype.Service;

@Service
public class SelfAutoHealingExternalServiceClient {

    /* see application.yml
  resilience4j.circuitbreaker:
    instances:
      externalService:
        registerHealthIndicator: true
        slidingWindowType: COUNT_BASED
        slidingWindowSize: 10
        failureRateThreshold: 50 # Open the circuit if 50% of calls fail
        waitDurationInOpenState: 5s # Stay open for 5 seconds
        permittedNumberOfCallsInHalfOpenState: 3 # Allow 3 calls in half-open state
        eventConsumerBufferSize: 10
     */
    @CircuitBreaker(name = "externalService", fallbackMethod = "fallbackResponse")
    @Retry(name = "externalService")
    public String callExternalService() {
        // Simulate failure
        if (Math.random() < 0.5) {
            throw new RuntimeException("External Service Failed");
        }
        return "Success";
    }

    public String fallbackResponse(Throwable t) {
        return "Service temporarily unavailable - using fallback";
    }
}
