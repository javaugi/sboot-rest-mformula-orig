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
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

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

    @KafkaListener(topics = "ordersunsafe", concurrency = "3")
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

    /*
    3) Redis — atomicity and distributed locks
    Unsafe pattern: read-modify-write without atomicity
    Problem: Two concurrent workers read same qty and both decrement → oversell.
     */
    @KafkaListener(topics = "ordersunsafe")
    public void unsafeReadModifyWriteWoAtomicity(String message) {
        String qtyStr = (String) redisTemplate.opsForValue().get("stock:sku:123");
        int qty = Integer.parseInt(qtyStr);
        if (qty >= 1) {
            redisTemplate.opsForValue().set("stock:sku:123", String.valueOf(qty - 1));
        }
    }

    /*
    Safe options
    Option A — Redis atomic commands (INCR/DECR) or Lua scripts
     */
    @KafkaListener(topics = "orderssafe")
    public void safeIncrDecrOrLuaScripts(String message) {
        // DECR returns the new value atomically
        Long remaining = 0L;
        //remaining = redisTemplate.execute((connection) -> connection.decr(StringRedisSerializer.UTF_8.serialize("stock:sku:123")));
        if (remaining < 0) {
            // rollback or re-increment and fail
        }

        /*
        or use a Lua script to check & decrement atomically:
            -- KEYS[1] = key, ARGV[1] = qtyToReserve
            local cur = tonumber(redis.call('GET', KEYS[1]) or '-1')
            if cur >= tonumber(ARGV[1]) then
              redis.call('DECRBY', KEYS[1], ARGV[1])
              return 1
            end
            return 0
         */
    }

    /*
    Option B — Redisson distributed lock
    Why safe: Redis atomic ops & Lua run on server atomically; distributed locks coordinate across instances.
     */
    @KafkaListener(topics = "orderssafe")
    public void safeUseLock(String message) {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");
        RedissonClient redisson = Redisson.create(config);

        try {
            RLock lock = (RLock) redisson.getLock("lock:stock:123");
            if (lock.tryLock(100, 10, TimeUnit.SECONDS)) {
                try {
                    // read & write safely
                    String qtyStr = (String) redisTemplate.opsForValue().get("stock:sku:123");
                    int qty = Integer.parseInt(qtyStr);
                    if (qty >= 1) {
                        redisTemplate.opsForValue().set("stock:sku:123", String.valueOf(qty - 1));
                    }
                } finally {
                    lock.unlock();
                }
            }
        } catch (InterruptedException ex) {
            //ignore
        }
    }
}
