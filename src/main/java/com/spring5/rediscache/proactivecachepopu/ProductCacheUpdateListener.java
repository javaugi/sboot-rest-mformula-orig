/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.rediscache.proactivecachepopu;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring5.entity.Product;
import static com.spring5.rediscache.proactivecachepopu.RedisCacheRefreshConfig.REDIS_TPL_PRODUCT;
import com.spring5.repository.ProductRepository;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductCacheUpdateListener {

    private final @Qualifier(REDIS_TPL_PRODUCT) RedisTemplate<String, Product> redisTemplate;
    private final ProductRepository productRepository;
    private final Opt1ProductCacheDebouncer productCacheDebouncer;
    private final ObjectMapper objectMapper;
    
    public static boolean option1 = false;

    @EventListener
    public void handleProductCacheUpdate(ProductCacheUpdateEvent event) {
        // Option 1: @Async + ScheduledExecutorService Debouncer (Java In-Memory)
        //🔹 Use Case: For single-node Spring Boot apps
        if (option1) {
            productCacheDebouncer.debounceUpdate(event.getProductId(), Duration.ofSeconds(10));
            return;
        }
        
        
        Long productId = event.getProductId();
        productRepository.findById(productId).ifPresent(product -> {
            try {
                String key = "product:" + productId;
                redisTemplate.opsForValue().set(key, product, Duration.ofMinutes(30));
                System.out.println("Cache proactively updated for product: " + productId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    

    public Product getProduct(Long id) {
        String key = "product:" + id;
        Product cached = redisTemplate.opsForValue().get(key);
        if (cached != null) {
            return cached;
        }

        // Optional fallback
        Product fresh = productRepository.findById(id).orElseThrow();
        redisTemplate.opsForValue().set(key, fresh, Duration.ofMinutes(30));
        return fresh;
    }    
    
    /*
    Strategies for Regional Caching (Spring + Redis or Caffeine)
       🧩 1. Region as Part of the Cache Key
       This is the simplest and most scalable approach.

       Example:
           String region = "US"; // or detect from user/session/header
           String cacheKey = "product:" + region + ":" + productId;
           All cache reads and writes are then scoped to that region.

       Example: Redis Cache with Regional Key    
       */   
    public Product getProduct(Long productId, String region) {
        String cacheKey = "product:" + region + ":" + productId;
        Product cached = redisTemplate.opsForValue().get(cacheKey);

        if (cached != null) return cached;

        // Fallback to DB
        Product product = productRepository.findById(productId).orElseThrow();
        redisTemplate.opsForValue().set(cacheKey, product, Duration.ofMinutes(30));
        return product;
    }    
    
    
    //Example: Spring @Cacheable with Custom Key Generator
   @Cacheable(value = "productCache", key = "#region + ':' + #productId")
   public Product getProductCache(Long productId, String region) {
       return productRepository.findById(productId).orElseThrow();
   }    
    
    //Example: Spring @Cacheable with Custom Key Generator
   @Cacheable(value = "productCache", keyGenerator = "regionKeyGenerator")
   public Product getProductCacheBYKeygen(Long productId, String region) {
       return productRepository.findById(productId).orElseThrow();
   }    
}

