/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.wmart.validrule;

import com.spring5.kafkamicroservice.Payment;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import lombok.Data;

public class DefaultPaymentValidator implements PaymentValidator {

    private final List<ValidationRule> rules;

    public DefaultPaymentValidator(List<ValidationRule> rules) {
        this.rules = rules;
    }

    @Override
    public boolean validatePayment(Payment payment) {
        // A simple implementation for the main method
        return getFailedRules(payment).isEmpty();
    }

    @Override
    public List<ValidationRule> getFailedRules(Payment payment) {
        return rules.stream().filter(rule -> !rule.validate(payment)).collect(Collectors.toList());
    }

    // @Override
    public List<ValidationRule> getFailedRulesInParalell(Payment payment) {
        return rules.parallelStream() // Use parallelStream()
                .filter(rule -> !rule.validate(payment))
                .collect(Collectors.toList());
    }

    // @Override
    public List<ValidationRule> getFailedRulesInComleteable(Payment payment) {
        List<CompletableFuture<ValidationResult>> futures
                = rules.stream()
                        .map(
                                rule
                                -> CompletableFuture.supplyAsync(
                                        () -> new ValidationResult(rule, rule.validate(payment))))
                        .collect(Collectors.toList());

        // Wait for all futures to complete and collect the results
        return futures.stream()
                .map(CompletableFuture::join)
                .filter(result -> !result.isValid())
                .map(ValidationResult::getRule)
                .collect(Collectors.toList());
    }

    @Data
    private static class ValidationResult {

        private final ValidationRule rule;
        private final boolean isValid;

        // Constructor, getters
        public ValidationResult(ValidationRule rule, boolean isValid) {
            this.rule = rule;
            this.isValid = isValid;
        }

        public boolean isValid() {
            return true;
        }
    }
}
