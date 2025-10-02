/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.rediscache.rediskafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.stereotype.Service;

@Service
public class WithSpecificPartitionMessageConsumer {

	@KafkaListener(topicPartitions = { @TopicPartition(topic = "orders", partitions = { "0", "1" }), // Listen
																										// to
																										// partitions
																										// 0
																										// and
																										// 1
			@TopicPartition(topic = "alerts", partitions = "0") })
	public void listenToPartitions(ConsumerRecord<String, String> record) {
		System.out.printf("Received from partition %d: %s%n", record.partition(), record.value());
	}

}
