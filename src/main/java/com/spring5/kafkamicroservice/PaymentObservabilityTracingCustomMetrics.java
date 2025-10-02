/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.kafkamicroservice;

import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

/**
 * @author javaugi
 */
public class PaymentObservabilityTracingCustomMetrics {

	// 3. Observability - Distributed Tracing:
	// See PaymentService
	@Bean
	public KafkaTemplate<String, String> kafkaTemplate(ProducerFactory<String, String> pf) {
		KafkaTemplate<String, String> template = new KafkaTemplate<>(pf);
		// template.setObservationEnabled(true);
		return template;
	}

	// Observability - Custom Metrics:
	@Bean
	public MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
		return registry -> registry.config()
			.commonTags("application", "payment-service", "region", System.getenv("REGION"));
	}

	@Timed(value = "payment.processing.time", description = "Time taken to process payment")
	public PaymentResult processPayment(PaymentRequest request) {
		// Payment processing
		return null;
	}

}
