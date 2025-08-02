/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.security.ratelimiterddos;

import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RateLimiterRequestOnSize {
    private static final int MAX_TOKENS = 100;
    private static final long WINDOW_SIZE_MILLIS = 60 * 1000;
    private final ConcurrentHashMap<String, Deque<Long>> userLimit = new ConcurrentHashMap<>();
    
    public static void main(String[] args) throws InterruptedException {
        RateLimiterRequestOnSize limiter = new RateLimiterRequestOnSize();
        String userId = "user123";

        for (int i = 0; i < 105; i++) {
            boolean allowed = limiter.isAllowed(userId);
            System.out.println("Request " + (i + 1) + ": " + (allowed ? "✅ Allowed" : "❌ Rate Limited"));
            Thread.sleep(500); // simulate delay
        }
    }    
    
    public boolean isAllowed(String userId) {
        return isAllowed(userId, MAX_TOKENS,WINDOW_SIZE_MILLIS);
    }

    public boolean isAllowed(String userId, int tokenLimit, long windowSize) {
        long now = Instant.now().toEpochMilli();
        Deque<Long> timestamps = userLimit.computeIfAbsent(userId, k -> new ArrayDeque<Long>());
        
        synchronized(timestamps) {
            //remove all those have exceeded the limit
            while (!timestamps.isEmpty() && (now - timestamps.peekFirst()) > windowSize) {
                timestamps.pollFirst();
            }
            
            if (timestamps.size() < tokenLimit) {
                timestamps.addLast(now);
                userLimit.put(userId, timestamps);
                return true;
            }            
        }
        
        return false;
    }
}
