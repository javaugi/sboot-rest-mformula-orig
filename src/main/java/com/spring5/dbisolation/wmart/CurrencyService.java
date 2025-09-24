/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.wmart;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.web.client.RestTemplate;

public class CurrencyService {

    private final RestTemplate restTemplate;

    public CurrencyService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // A Circuit Breaker with a fallback method.
    // If the call to the external currency service fails, the fallback method is executed.
    @CircuitBreaker(name = "currencyService", fallbackMethod = "defaultRate")
    public double getExchangeRate(String from, String to) {
        // A real-world call to an external API
        String url = String.format("http://currency-exchange-api/rate?from=%s&to=%s", from, to);
        return restTemplate.getForObject(url, Double.class);
    }

    // Fallback method that provides a default, safe value
    private double defaultRate(String from, String to, Throwable t) {
        // Log the failure for analysis
        System.err.println("Circuit breaker is open or call failed. Returning default rate. Error: " + t.getMessage());
        return 1.0; // Return a default value to prevent application failure
    }
}
