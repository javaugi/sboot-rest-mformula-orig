/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.wmart;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class PaymentService {

    private final ExternalCurrencyServiceClient currencyClient;

    public PaymentService(ExternalCurrencyServiceClient currencyClient) {
        this.currencyClient = currencyClient;
    }

    // Using Resilience4j annotations - much cleaner
    @CircuitBreaker(name = "currencyService", fallbackMethod = "getCachedConversionRate")
    @Retry(name = "currencyService", fallbackMethod = "getCachedConversionRate")
    @TimeLimiter(name = "currencyService")
    public CompletableFuture<BigDecimal> getConvertedAmount(String fromCurrency, String toCurrency, BigDecimal amount) {
        return CompletableFuture.supplyAsync(() -> {
            BigDecimal rate = currencyClient.getConversionRate(fromCurrency, toCurrency);
            return amount.multiply(rate);
        });
    }

    // Fallback method
    private CompletableFuture<BigDecimal> getCachedConversionRate(String fromCurrency, String toCurrency, BigDecimal amount, Exception e) {
        // Logic to get a stale-but-acceptable rate from a local cache
        BigDecimal cachedRate = getRateFromCache(fromCurrency, toCurrency);
        return CompletableFuture.completedFuture(amount.multiply(cachedRate));
    }

    private BigDecimal getRateFromCache(String from, String to) {
        // Implementation to get from Redis/Cache
        return BigDecimal.ONE; // simplified
    }

    /*
    public synchronized Mono<PaymentResponse> processPayment(PaymentRequest request) {
        return Mono.just(PaymentResponse.builder().build());
    }
    // */
    public synchronized Mono<PaymentResponse> processPayment(PaymentRequest request) {
        // Implementation
        return Mono.just(PaymentResponse.builder().build());
    }
}
