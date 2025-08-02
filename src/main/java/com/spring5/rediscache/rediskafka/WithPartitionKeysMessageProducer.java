/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.rediscache.rediskafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WithPartitionKeysMessageProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendOrderEvent(KafkaOrderEvent event) {
        // Use orderId as key to ensure same order always goes to same partition
        kafkaTemplate.send("orders", event.getOrderId(), event.toString());

        // Or explicitly specify partition
        // kafkaTemplate.send("orders", 0, event.getOrderId(), event.toString());
    }    
}
