/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.rediscache.rediskafka;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.RedisTemplate;

@Service
@RequiredArgsConstructor
public class KafkaOrderedConsumer {

    private static final String SEQUENCE_PREFIX = "kafka:sequence:";
    private final @Qualifier(RedisWithKafkaConfig.REDIS_TPL_LONG) RedisTemplate<String, Long> redisTemplate;

    @KafkaListener(topics = "ordered-events")
    public void processOrderedEvent(KafkaOrderedEvent event) {
        String sequenceKey = SEQUENCE_PREFIX + event.getEntityId();
        
        // Get last processed sequence number from Redis
        Long lastSequence = redisTemplate.opsForValue().get(sequenceKey);
        lastSequence = lastSequence == null ? 0L : lastSequence;
        
        // Check if this event is in order
        if (event.getSequenceNumber() == lastSequence + 1) {
            // Process the event
            processEvent(event);
            
            // Update the sequence number in Redis
            redisTemplate.opsForValue().set(sequenceKey, event.getSequenceNumber());
        } else if (event.getSequenceNumber() > lastSequence + 1) {
            // Store in Redis for later processing (out-of-order)
            redisTemplate.opsForList().rightPush(
                "kafka:out-of-order:" + event.getEntityId(), event.getSequenceNumber());
        }
        // Else (duplicate or old event) - ignore
    }

    private void processEvent(KafkaOrderedEvent event) {
        // Your business logic here
    }
}
