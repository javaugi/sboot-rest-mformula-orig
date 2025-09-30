/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.rediscache.proactivecachepopu;

// Option 2: Use Caffeine Cache as a Debounce Map
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.concurrent.TimeUnit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// ✅ Option 2: Use Caffeine Cache as a Debounce Map
// 🔹 Use Case: Memory-safe expiration, avoids ConcurrentHashMap
@Configuration
public class Opt2DebounceCacheConfig {

    @Bean
    public Cache<Long, Boolean> debounceCache() {
        return Caffeine.newBuilder().expireAfterWrite(10, TimeUnit.SECONDS).maximumSize(10_000).build();
    }
}
