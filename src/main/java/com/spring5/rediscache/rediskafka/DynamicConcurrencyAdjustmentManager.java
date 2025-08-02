/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.rediscache.rediskafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.stereotype.Service;

@Service 
@RequiredArgsConstructor 
@Slf4j
public class DynamicConcurrencyAdjustmentManager {

    private final ConcurrentKafkaListenerContainerFactory<String, String> containerFactory;

    private final KafkaListenerEndpointRegistry registry;

    public void adjustConcurrency(String listenerId, int newConcurrency) {
        MessageListenerContainer container = registry.getListenerContainer(listenerId);
        if (container instanceof ConcurrentMessageListenerContainer) {
            ((ConcurrentMessageListenerContainer<?, ?>) container).setConcurrency(newConcurrency);
            log.info("Adjusted concurrency for listener {} to {}", listenerId, newConcurrency);
        }
    }
}

