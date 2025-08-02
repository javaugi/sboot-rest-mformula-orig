/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.outbox;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    // Method to send to default topic
    public void sendToDefaultTopic(String message) {
        kafkaTemplate.send("my-default-topic", message);
    }

    // Method to send to outbox topic (with key for compaction)
    public void sendToOutboxTopic(String key, String message) {
        kafkaTemplate.send("my-outbox-topic", key, message);
    }
}
