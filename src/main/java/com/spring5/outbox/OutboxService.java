/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.outbox;

import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OutboxService {

	private final OutboxRepository outboxRepository;

	private final KafkaProducerService kafkaProducerService;

	public OutboxService(OutboxRepository outboxRepository, KafkaProducerService kafkaProducerService) {
		this.outboxRepository = outboxRepository;
		this.kafkaProducerService = kafkaProducerService;
	}

	@Transactional
	public void createOutboxEvent(String aggregateType, String aggregateId, String eventType, String payload) {
		Outbox outbox = new Outbox();
		outbox.setAggregateType(aggregateType);
		outbox.setAggregateId(aggregateId);
		outbox.setEventType(eventType);
		outbox.setPayload(payload);
		outbox.setCreatedAt(LocalDateTime.now());

		outboxRepository.save(outbox);

		// Send to Kafka
		kafkaProducerService.sendToOutboxTopic(aggregateId, payload);
	}

}
