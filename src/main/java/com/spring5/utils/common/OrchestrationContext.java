/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.utils.common;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;


public record OrchestrationContext(
    String correlationId,
    ReactiveClaimEvent claimEvent,
    Map<String, Object> processVariables,
    LocalDateTime timestamp ,
    List<OrchestrationStep> completedSteps,  // Track progress
    OrchestrationStatus status // IN_PROGRESS, COMPLETED, FAILED


    ) {

    public OrchestrationContext

    {
        // Default values in compact constructor
        if (processVariables == null) {
            processVariables = new ConcurrentHashMap<>();
        }
        if (completedSteps == null) {
            completedSteps = new CopyOnWriteArrayList<>();
        }
        if (status == null) {
            status = OrchestrationStatus.IN_PROGRESS;
        }
    }

    // Helper method to add a process variable
    public OrchestrationContext withVariable(String key, Object value) {
        Map<String, Object> newVariables = new ConcurrentHashMap<>(this.processVariables);
        newVariables.put(key, value);

        return new OrchestrationContext(
            this.correlationId,
            this.claimEvent,
            newVariables,
            this.timestamp,
            this.completedSteps,
            this.status
        );
    }

    // Helper method to add a completed step
    public OrchestrationContext withCompletedStep(String stepName) {
        List<OrchestrationStep> newSteps = new CopyOnWriteArrayList<>(this.completedSteps);
        newSteps.add(new OrchestrationStep(stepName, LocalDateTime.now()));

        return new OrchestrationContext(
            this.correlationId,
            this.claimEvent,
            this.processVariables,
            this.timestamp,
            newSteps,
            this.status
        );
    }

    // Helper method to create a new instance with updated status
    public OrchestrationContext withStatus(OrchestrationStatus newStatus) {
        return new OrchestrationContext(
            this.correlationId,
            this.claimEvent,
            this.processVariables,
            this.timestamp,
            this.completedSteps,
            newStatus
        );
    }
}
