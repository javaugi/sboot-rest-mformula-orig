/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.rediscache.proactivecachepopu;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Opt3RedisDebounceService {
    private final StringRedisTemplate redis;

    public boolean shouldUpdate(Long productId, Duration debounceWindow) {
        String key = "debounce:product:" + productId;
        Boolean set = redis.opsForValue().setIfAbsent(key, "1", debounceWindow);
        return Boolean.TRUE.equals(set); // only true if key did not exist
    }    
}
