/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.security.threadsafety;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/*
Safe consumer patterns
    1. Use stateless handling and thread-safe stores (Redis, DB).
    2. If in-memory caching is required, use ConcurrentHashMap or external cache.
    3. Ensure idempotency: consumers should be able to process duplicates safely (use dedupe store).
 */

@Component
public class ThreadSafeConsumer {

    //Unsafe consumer example: non-thread-safe repository usage - the following are not thread safe
    private final CacheLoader<String, String> loader = key -> {
        System.out.println("Loading value for key: " + key);
        // Simulate an expensive operation (e.g., database lookup, network call)
        return key;
    };

    private final LoadingCache<String, String> refCache = Caffeine.newBuilder()
            .maximumSize(100) // Set maximum number of entries
            .expireAfterWrite(10, TimeUnit.MINUTES) // Entries expire 10 minutes after writing
            .build(loader); // Build the cache with the defined loader

    @Autowired
    RedisTemplate redisTemplate;

    @KafkaListener(topics = "orders", concurrency = "3")
    public void handleUnsafe(String message) {
        // update cache (not thread-safe) -> race
        refCache.put(extractId(message), message);
    }

    private String extractId(String message) {
        return message;
    }

    /*
    Example — idempotent consumer using Redis SETNX
    Why safe: SETNX is atomic in Redis; only one consumer will succeed and proceed.
     */
    @KafkaListener(topics = "ordersafe")
    public void handleSafe(String message) {
        String eventId = extractId(message);
        // Use Redis SETNX to ensure single processing
        Boolean taken = redisTemplate.opsForValue().setIfAbsent("processed:" + eventId, "1", Duration.ofHours(1));
        if (Boolean.FALSE.equals(taken)) {
            // already processed => idempotent
            return;
        }
        // process message
    }
}
