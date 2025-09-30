/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.rediscache.rediskafka;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaBatchConcurrentConsumerService {
    // Batch listener implementation

    @KafkaListener(
            topics = "${kafka.topic.batch}",
            containerFactory = "batchKafkaListenerContainerFactory")
    public void consumeBatch(List<ConsumerRecord<String, String>> records, Acknowledgment ack) {
        log.info("Received batch of {} messages", records.size());

        for (ConsumerRecord<String, String> record : records) {
            try {
                processMessage(record.value());
            } catch (Exception e) {
                log.error("Error processing message in batch: {}", record.value(), e);
            }
        }

        ack.acknowledge(); // Acknowledge the entire batch
    }

    private void processMessage(String message) {
    }
}
