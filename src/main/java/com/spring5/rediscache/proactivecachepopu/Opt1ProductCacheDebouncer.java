/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.rediscache.proactivecachepopu;

import com.spring5.entity.Product;
import static com.spring5.rediscache.proactivecachepopu.RedisCacheRefreshConfig.REDIS_TPL_PRODUCT;
import com.spring5.repository.ProductRepository;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/*
 let’s explore three effective ways to debounce or throttle cache updates to avoid duplicate or excessive writes during high-frequency updates.

These apply when:
    You receive multiple update events for the same cache key in a short time
    You want to batch or skip redundant refreshes

✅ Option 1: @Async + ScheduledExecutorService Debouncer (Java In-Memory)
🔹 Use Case: For single-node Spring Boot apps
 */

 /*
Summary Comparison
Method                      Scope               Best for                    TTL Cleanup
ScheduledExecutorService	In-memory           Single instance             Manual
Caffeine expireAfterWrite	In-memory           Auto-expire, clean design	Auto
Redis SETNX + EXPIRE        Distributed-safe	Multi-instance clusters     Auto

Would you like me to integrate this into your existing Spring Boot + Redis project structure with a full working sample?
 */
@Component
@RequiredArgsConstructor
public class Opt1ProductCacheDebouncer {
    // Option 1: @Async + ScheduledExecutorService Debouncer (Java In-Memory)

    // Use Case: For single-node Spring Boot apps
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final Map<Long, ScheduledFuture<?>> debounceMap = new ConcurrentHashMap<>();

    private final ProductRepository repository;
    private final @Qualifier(REDIS_TPL_PRODUCT)
    RedisTemplate<String, Product> redisTemplate;

    /* debounce or throttle cache updates to avoid duplicate or excessive writes during high-frequency updates.

      These apply when:
          You receive multiple update events for the same cache key in a short time
          You want to batch or skip redundant refreshes
     */
    public void debounceUpdate(Long productId, Duration debounceWindow) {
        ScheduledFuture<?> existingTask = debounceMap.get(productId);
        if (existingTask != null && !existingTask.isDone()) {
            existingTask.cancel(false); // cancel previous task
        }

        ScheduledFuture<?> newTask
                = scheduler.schedule(
                        () -> {
                            try {
                                Product product = repository.findById(productId).orElseThrow();
                                String key = "product:" + productId;
                                redisTemplate.opsForValue().set(key, product, Duration.ofMinutes(30));
                                System.out.println("🔄 Cache refreshed for product: " + productId);
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                debounceMap.remove(productId); // clean up
                            }
                        },
                        debounceWindow.toMillis(),
                        TimeUnit.MILLISECONDS);

        debounceMap.put(productId, newTask);
    }
}
