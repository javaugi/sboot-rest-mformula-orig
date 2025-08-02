/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.rediscache.rediskafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaConcurrentConsumerService {
    // Basic concurrent listener
    @KafkaListener(
        topics = "${kafka.topic.consumer}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(ConsumerRecord<String, String> record, Acknowledgment ack) {
        try {
            log.info("Received message on thread {}: key={}, value={}, partition={}, offset={}",
                Thread.currentThread().getName(),
                record.key(),
                record.value(),
                record.partition(),
                record.offset());

            // Process your message here
            processMessage(record.value());

            // Manually acknowledge the message
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Error processing message: {}", record.value(), e);
            // Handle error (e.g., send to DLQ)
        }
    }

    private void processMessage(String message) {
        // Your business logic here
        log.info("Processing message: {}", message);
    }    
}
