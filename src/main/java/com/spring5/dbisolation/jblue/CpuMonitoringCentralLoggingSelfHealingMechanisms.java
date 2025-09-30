/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.jblue;

/*
Putting It All Together

With:
    Micrometer + Actuator → CPU metrics
    Structured JSON Logging → Production-grade logs
    Resilience4j + K8s Probes → Self-healing
 */
public class CpuMonitoringCentralLoggingSelfHealingMechanisms {

    /*
  Break this into three major parts with code examples:
      1. CPU Monitoring (via Micrometer + Actuator)
          class CpuMonitoring
      2. Centralized Logging (with structured JSON logs)
          class CentralizedLoggingCorrelationIdFilter and logback-spring.xml in src/main/resources
      3. Self-Healing Mechanisms (circuit breaker, auto-restarts, retries)
              (1) Retries for transient failures
              (2) Circuit breaker to prevent cascading failures
                  Use Resilience4j for circuit breakers + retries.
                  class SelfHealingExternalServiceClient
                  Application Properties
                  resilience4j.retry.instances.externalService.max-attempts=3
                  resilience4j.circuitbreaker.instances.externalService.slidingWindowSize=5
                  resilience4j.circuitbreaker.instances.externalService.failureRateThreshold=50
                  resilience4j.circuitbreaker.instances.externalService.waitDurationInOpenState=10s
              (2) Automatic restart when failures exceed threshold
     */
    public void way1CPUMonitoringViaMicrometerActuator() {
    }

    public void way2CentralLoggingWithJSONAndCorrelationIDs() {
    }

    public void way2SelfHealingViaRetriesCircuitBreaker() {
    }

    public void way2AutoHealingViaKubernetesProbes() {
        /*
    4. Auto-Healing via Kubernetes Probes

    When deploying to Kubernetes, add liveness and readiness probes in deployment.yaml:

    livenessProbe:
      httpGet:
        path: /actuator/health/liveness
        port: 8080
      initialDelaySeconds: 30
      periodSeconds: 10
    readinessProbe:
      httpGet:
        path: /actuator/health/readiness
        port: 8080
      initialDelaySeconds: 20
      periodSeconds: 5


    This way, Kubernetes restarts pods automatically if health checks fail.


    2. Enable Liveness & Readiness Probes

    Spring Boot has built-in health groups for liveness and readiness.
    Add this in application.yml:

    management:
      endpoints:
        web:
          exposure:
            include: health
      endpoint:
        health:
          probes:
            enabled: true   # Enables liveness and readiness probes

    This automatically creates:
        /actuator/health/liveness
        /actuator/health/readiness

    4. Test in Browser or cURL
    curl http://localhost:8080/actuator/health/liveness
    curl http://localhost:8080/actuator/health/readiness
         */
    }
}
