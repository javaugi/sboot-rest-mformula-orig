/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.utils.common;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReactiveOrchestrationCoordinator {

    /*
  Benefits of OrchestrationContext:
      Auditability: Complete trace of what happened during orchestration
      Resilience: Enough data to compensate/rollback failed operations
      Debugging: Centralized place to see orchestration state
      Monitoring: Track progress and performance of each orchestration
      Idempotency: Correlation ID helps prevent duplicate processing
     */
    private final Map<String, OrchestrationContext> activeProcesses = new ConcurrentHashMap<>();

    public Mono<Void> initiateOrchestration(ReactiveClaimEvent claimEvent) {
        String correlationId = generateCorrelationId(claimEvent.id);

        OrchestrationContext context
                = new OrchestrationContext(
                        correlationId,
                        claimEvent,
                        new ConcurrentHashMap<>(),
                        LocalDateTime.now(),
                        new CopyOnWriteArrayList<>(),
                        OrchestrationStatus.INITIATED);

        activeProcesses.put(correlationId, context);

        return processOrchestrationSteps(context)
                .doOnSuccess(
                        result -> {
                            OrchestrationContext updatedContext
                            = context
                                    .withCompletedStep("FINAL")
                                    .withVariable("endTime", LocalDateTime.now())
                                    .withStatus(OrchestrationStatus.COMPLETED);

                            activeProcesses.put(correlationId, updatedContext);
                        })
                .doOnError(
                        error -> {
                            OrchestrationContext failedContext
                            = context
                                    .withStatus(OrchestrationStatus.FAILED)
                                    .withVariable("error", error.getMessage());

                            activeProcesses.put(correlationId, failedContext);
                            triggerCompensation(failedContext);
                        });
    }

    private String generateCorrelationId(String claimId) {
        return claimId;
    }

    private Mono<Void> processOrchestrationSteps(OrchestrationContext context) {
        return Mono.defer(
                () -> {
                    ReactiveClaimEvent event = context.claimEvent();

                    return validateClaim(event, context)
                            .doOnNext(
                                    result -> {
                                        context
                                                .withCompletedStep("VALIDATION")
                                                .withVariable("validationResult", result);
                                    })
                            .then(applyClinicalRules(event, context))
                            .doOnNext(
                                    rulesResult -> {
                                        context
                                                .withCompletedStep("CLINICAL_RULES")
                                                .withVariable("rulesApplied", rulesResult);
                                    })
                            .then(processPayment(event, context))
                            .doOnNext(
                                    paymentResult -> {
                                        context.withCompletedStep("PAYMENT").withVariable("paymentId", paymentResult);
                                    })
                            .then();
                });
    }

    // Update the  Record Instance by withStatus
    // Update the Usage in OrchestrationCoordinator
    private Mono<Void> processOrchestrationSteps2(OrchestrationContext context) {
        return Mono.defer(
                () -> {
                    ReactiveClaimEvent event = context.claimEvent();

                    return validateClaim(event, context)
                            .map(
                                    validationResult -> {
                                        // Update context with validation result
                                        return context
                                                .withCompletedStep("VALIDATION")
                                                .withVariable("validationResult", validationResult);
                                    })
                            .flatMap(
                                    updatedContext -> {
                                        activeProcesses.put(updatedContext.correlationId(), updatedContext);
                                        return applyClinicalRules(event, updatedContext);
                                    })
                            .map(
                                    rulesResult -> {
                                        return context
                                                .withCompletedStep("CLINICAL_RULES")
                                                .withVariable("rulesApplied", rulesResult);
                                    })
                            .flatMap(
                                    updatedContext -> {
                                        activeProcesses.put(updatedContext.correlationId(), updatedContext);
                                        return processPayment(event, updatedContext);
                                    })
                            .map(
                                    paymentResult -> {
                                        return context
                                                .withCompletedStep("PAYMENT")
                                                .withVariable("paymentId", paymentResult);
                                    })
                            .then();
                });
    }

    // Functional Approach with Context Passing
    private Mono<OrchestrationContext> processOrchestrationSteps3(OrchestrationContext context) {
        return validateClaim(context.claimEvent())
                .map(
                        validationResult
                        -> context
                                .withCompletedStep("VALIDATION")
                                .withVariable("validationResult", validationResult))
                .flatMap(
                        updatedContext
                        -> applyClinicalRules(updatedContext.claimEvent())
                                .map(
                                        rulesResult
                                        -> updatedContext
                                                .withCompletedStep("CLINICAL_RULES")
                                                .withVariable("rulesApplied", rulesResult)))
                .flatMap(
                        updatedContext
                        -> processPayment(updatedContext.claimEvent())
                                .map(
                                        paymentResult
                                        -> updatedContext
                                                .withCompletedStep("PAYMENT")
                                                .withVariable("paymentId", paymentResult)
                                                .withStatus(OrchestrationStatus.COMPLETED)))
                .doOnNext(
                        updatedContext -> activeProcesses.put(updatedContext.correlationId(), updatedContext));
    }

    private Mono<ReactiveClaimEvent> validateClaim(ReactiveClaimEvent event) {
        return Mono.justOrEmpty(event);
    }

    private Mono<OrchestrationContext> validateClaim(
            ReactiveClaimEvent event, OrchestrationContext context) {
        return Mono.justOrEmpty(context);
    }

    private Mono<ReactiveClaimEvent> applyClinicalRules(ReactiveClaimEvent event) {
        return Mono.justOrEmpty(event);
    }

    private Mono<OrchestrationContext> applyClinicalRules(
            ReactiveClaimEvent event, OrchestrationContext context) {
        return Mono.justOrEmpty(context);
    }

    private Mono<ReactiveClaimEvent> processPayment(ReactiveClaimEvent event) {
        return Mono.justOrEmpty(event);
    }

    private Mono<OrchestrationContext> processPayment(
            ReactiveClaimEvent event, OrchestrationContext context) {
        return Mono.justOrEmpty(context);
    }

    private void triggerCompensation(OrchestrationContext context) {
        // Use context data to undo completed steps
        log.warn("Starting compensation for orchestration: {}", context.correlationId());

        if (context.completedSteps().stream().anyMatch(step -> step.stepName().equals("PAYMENT"))) {

            String paymentId = (String) context.processVariables().get("paymentId");
            compensatePayment(paymentId, context);
        }

        // Update status to COMPENSATING
        OrchestrationContext compensatingContext = context.withStatus(OrchestrationStatus.COMPENSATING);
        activeProcesses.put(context.correlationId(), compensatingContext);
    }

    private void compensatePayment(String paymentId, OrchestrationContext context) {
    }
}
