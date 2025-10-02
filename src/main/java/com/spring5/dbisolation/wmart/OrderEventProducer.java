/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.wmart;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

@Service
public class OrderEventProducer {

	private final KafkaTemplate<String, String> kafkaTemplate;

	public OrderEventProducer(KafkaTemplate<String, String> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
	}

	public CompletableFuture<SendResult<String, String>> publishOrderEvent(String storeId, String messageId,
			String payload) {
		ProducerRecord<String, String> record = new ProducerRecord<>("order-events", storeId, payload);
		record.headers().add("messageId", messageId.getBytes(StandardCharsets.UTF_8));
		return kafkaTemplate.send(record); // .completable();
	}

}
