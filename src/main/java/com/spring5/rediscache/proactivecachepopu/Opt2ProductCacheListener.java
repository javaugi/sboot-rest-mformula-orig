/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.rediscache.proactivecachepopu;

import com.github.benmanes.caffeine.cache.Cache;
import com.spring5.entity.Product;
import static com.spring5.rediscache.proactivecachepopu.RedisCacheRefreshConfig.REDIS_TPL_PRODUCT;
import com.spring5.repository.ProductRepository;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

// Option 2 Listener Example
@Component
@RequiredArgsConstructor
public class Opt2ProductCacheListener {

    private final Cache<Long, Boolean> debounceCache;
    private final ProductRepository repo;
    private final @Qualifier(REDIS_TPL_PRODUCT)
    RedisTemplate<String, Product> redis;
    private final Opt3RedisDebounceService debounceService;

    public static boolean option3 = false;

    @EventListener
    public void onUpdate(ProductCacheUpdateEvent event) {
        if (option3) {
            onUpdateOpt3(event);
            return;
        }

        Long productId = event.getProductId();
        if (debounceCache.getIfPresent(productId) != null) {
            System.out.println("⏳ Skipped duplicate update for: " + productId);
            return;
        }

        // Mark as seen
        debounceCache.put(productId, true);

        // Update Redis
        Product product = repo.findById(productId).orElseThrow();
        redis.opsForValue().set("product:" + productId, product, Duration.ofMinutes(30));
        System.out.println("✅ Cache written for: " + productId);
    }

    public void onUpdateOpt3(ProductCacheUpdateEvent event) {
        Long productId = event.getProductId();
        if (!debounceService.shouldUpdate(productId, Duration.ofSeconds(10))) {
            System.out.println("⏳ Skipped Redis-locked update for " + productId);
            return;
        }

        Product product = repo.findById(productId).orElseThrow();
        redis.opsForValue().set("product:" + productId, product, Duration.ofMinutes(30));
        System.out.println("✅ Redis cache updated for " + productId);
    }
}
