/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.rediscache.rediskafka;

import com.spring5.KafkaBaseConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

@Service
@RequiredArgsConstructor
@Slf4j
public class WithPartitionOrderProcessingSystem {

	// Kafka Producer - partitions orders by customer ID

	private final WithPartitionRedisPartitionerConsistentHashing redisPartitioner;

	private final @Qualifier(KafkaBaseConfig.KAFKA_TMPL_STR) KafkaTemplate kafkaTemplate;

	private final WithPartitionOrderService orderService;

	public void processOrder(WithPartitionOrder order) {
		// Send to partition based on customerId for ordering
		kafkaTemplate.send("orders", order.getCustomerId(), order.toJson());

		// Store in Redis - sharded by orderId
		try (Jedis jedis = redisPartitioner.getConnection(order.getId())) {
			jedis.setex("order:" + order.getId(), 3600, order.toJson());
		}
	}

	// Kafka Consumer - processes orders concurrently
	@KafkaListener(topics = "orders", concurrency = "3")
	public void handleOrder(String orderJson, Acknowledgment ack) {
		WithPartitionOrder order = WithPartitionOrder.fromJson(orderJson);

		try (Jedis jedis = redisPartitioner.getConnection(order.getId())) {
			// Check if order was already processed
			if (jedis.exists("processed:" + order.getId())) {
				return;
			}

			// Process order
			orderService.process(order);

			// Mark as processed
			jedis.setex("processed:" + order.getId(), 86400, "1");
			ack.acknowledge();
		}
	}

}
