/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.rediscache.rediskafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service 
public class DynamicConcurrencyListener {

    // Then in your listener:
    @KafkaListener(
        id = "myListener", // Important to set an ID for dynamic control
        topics = "${kafka.topic.consumer}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeWithDynamicConcurrency(ConsumerRecord<String, String> record) {
        // Your processing logic
    } 
}

