/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.rediscache.rediskafka;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisBackedIdempotencyService {

    private final @Qualifier(RedisWithKafkaConfig.REDIS_TPL_OBJ)
    RedisTemplate<String, Object> redisTemplate;
    private static final String IDEMPOTENCY_PREFIX = "idempotency:";
    private static final long IDEMPOTENCY_KEY_TTL = 24; // hours

    public boolean isDuplicate(String idempotencyKey) {
        return redisTemplate.hasKey(IDEMPOTENCY_PREFIX + idempotencyKey);
    }

    public void storeResponse(String idempotencyKey, Object response) {
        redisTemplate
                .opsForValue()
                .set(IDEMPOTENCY_PREFIX + idempotencyKey, response, IDEMPOTENCY_KEY_TTL, TimeUnit.HOURS);
    }

    public Object getResponse(String idempotencyKey) {
        return redisTemplate.opsForValue().get(IDEMPOTENCY_PREFIX + idempotencyKey);
    }
}
