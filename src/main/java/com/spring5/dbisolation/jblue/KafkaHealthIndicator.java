/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.jblue;

// 4. A Custom Health Indicator
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

// This is critical for the "production readiness" and "self-monitoring" requirements.
// Kubernetes uses this to check if the application is healthy.
@Component
class KafkaHealthIndicator implements HealthIndicator {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private static final Logger logger = LoggerFactory.getLogger(KafkaHealthIndicator.class);

    @Autowired
    public KafkaHealthIndicator(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public Health health() {
        try {
            // Attempt to send a message to check connectivity.
            // A real implementation would be more robust, potentially checking consumer lag as well.
            kafkaTemplate.send("health-check-topic", "ping");
            return Health.up().withDetail("kafka-status", "Connected").build();
        } catch (Exception e) {
            logger.error("Kafka connection is down: {}", e.getMessage());
            // If the connection fails, the health status is DOWN, signaling a problem to Kubernetes.
            return Health.down().withDetail("kafka-status", "Disconnected").withException(e).build();
        }
    }
}
