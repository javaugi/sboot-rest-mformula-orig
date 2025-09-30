/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.rediscache.rediskafka;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScheduledOutOfOrderEventProcessor {

    private final @Qualifier(RedisWithKafkaConfig.REDIS_TPL_OBJ)
    RedisTemplate<String, Object> redisTemplate;
    private final KafkaOrderedConsumer eventConsumer;

    @Scheduled(fixedRate = 5000) // Run every 5 seconds
    public void processOutOfOrderEvents() {
        // Get all entity IDs with out-of-order events
        Set<String> entityKeys = redisTemplate.keys("kafka:out-of-order:*");

        for (String key : entityKeys) {
            String entityId = key.substring("kafka:out-of-order:".length());
            String sequenceKey = "kafka:sequence:" + entityId;

            Long lastSequence = (Long) redisTemplate.opsForValue().get(sequenceKey);
            lastSequence = lastSequence == null ? 0L : lastSequence;

            // Peek at the next event without removing it
            KafkaOrderedEvent nextEvent = (KafkaOrderedEvent) redisTemplate.opsForList().index(key, 0);

            if (nextEvent != null && nextEvent.getSequenceNumber() == lastSequence + 1) {
                // Process the event
                eventConsumer.processOrderedEvent(nextEvent);

                // Remove the processed event
                redisTemplate.opsForList().leftPop(key);
            }
        }
    }
}
