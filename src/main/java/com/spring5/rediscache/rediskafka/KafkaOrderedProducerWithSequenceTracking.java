/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.rediscache.rediskafka;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaOrderedProducerWithSequenceTracking {

	private final @Qualifier(KafkaRedisConfig.REDIS_KAFKA_ORDER_EVENT_TMPL) KafkaTemplate<String, KafkaOrderedEvent> kafkaTemplate;

	private final @Qualifier(RedisWithKafkaConfig.REDIS_TPL_LONG) RedisTemplate<String, Long> redisTemplate;

	public void sendOrderedEvent(String entityId, KafkaOrderedEvent event) {
		// Get and increment sequence number atomically
		String sequenceKey = "kafka:producer:sequence:" + entityId;
		Long sequenceNumber = redisTemplate.opsForValue().increment(sequenceKey);

		// Set the sequence number in the event
		event.setSequenceNumber(sequenceNumber);
		event.setEntityId(entityId);

		// Send to Kafka with entityId as key to ensure ordering
		kafkaTemplate.send("ordered-events", entityId, event);
	}

}
