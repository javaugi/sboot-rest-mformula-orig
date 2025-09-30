/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.outbox;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    // Listener for default topic
    @KafkaListener(topics = "my-default-topic", groupId = "my-group")
    public void consumeDefaultTopic(String message) {
        System.out.println("Received message from default topic: " + message);
        // Process the message
    }

    // Listener for outbox topic
    @KafkaListener(topics = "my-outbox-topic", groupId = "my-group")
    public void consumeOutboxTopic(String message) {
        System.out.println("Received message from outbox topic: " + message);
        // Process the outbox message
    }
}
