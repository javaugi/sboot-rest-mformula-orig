/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.wmart;

// Consumer Service
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class FraudDetectionConsumer {

    @KafkaListener(topics = "international-transactions-topic", groupId = "fraud-detection-group")
    public void listen(String message) {
        System.out.println("Received transaction for fraud detection: " + message);
        // Implement idempotent fraud detection logic here
    }
}
