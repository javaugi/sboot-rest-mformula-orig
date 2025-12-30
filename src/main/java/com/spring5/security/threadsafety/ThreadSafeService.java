/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.security.threadsafety;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring5.audit.AuditOrder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/*
Thread Safety in Spring Components
Component                               Scope                       Thread-Safe?         Notes
@Controller, @Service, @Repository      Singleton                   ❌                   Don’t use mutable instance fields
@Component with @Scope("prototype")     New instance per request	✅ (per use)         Use for request-specific state
@Component with @Scope("request")       New instance per request	✅ (per use)         Use for request-specific state and inherently thread-safe since no sharing occurs
ThreadLocal                             Per-thread                  ✅                   Use sparingly (leaks in thread pools)

⚙️ 2. Common Thread Safety Issues in Java Spring Boot Microservices
Issue 1 — Shared Mutable State in Singleton Beans
    • Storing per-request data in static fields or instance variables.
    • Fix: Keep request data local; use immutable DTOs.
Issue 2 — Non-thread-safe Collections
    • Using HashMap, ArrayList, etc. in concurrent contexts.
    • Fix: Use ConcurrentHashMap, CopyOnWriteArrayList, or synchronization.
Issue 3 — Caching & Redis Race Conditions
    • Multiple threads updating shared cache keys.
    • Fix: Use Redis atomic operations, distributed locks (e.g., Redisson), or Lua scripts.
Issue 4 — Kafka Consumers and Producers
    • KafkaTemplate is thread-safe ✅
    • But consumer message handlers must be stateless or use synchronized blocks when updating shared resources.
Issue 5 — Async and WebFlux Threading
    • Reactive pipelines use multiple threads; don’t rely on ThreadLocal or mutable static fields.


🧠 4. Best Practices Summary
Layer                   Thread Safety Practice              Tools/Techniques
Spring Boot Controller	Keep stateless                      No mutable instance fields
Service Layer           Immutable operations                ConcurrentHashMap, Atomic*
DAO/Repository          Leverage DB transactions            @Transactional for atomicity
Kafka                   Idempotent consumers                Kafka exactly-once semantics
Redis                   Distributed locks                   Redisson, Lua scripts
WebFlux                 Immutable pipelines                 Avoid ThreadLocal
React                   Prevent race in state               useEffect cleanup, AbortController

 */
@Slf4j
@lombok.RequiredArgsConstructor
@Service
public class ThreadSafeService {
    private final Map<Long, Integer> inMemoryStock = new HashMap<>(); // not thread-safe

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper mapper = new ObjectMapper();

    @KafkaListener(topics = "orders")
    public void processOrder(String message) throws Exception {
        AuditOrder order = mapper.readValue(message, AuditOrder.class);
        String key = "order:" + order.getId();
        // Atomic update
        redisTemplate.opsForValue().setIfAbsent(key, message);
    }

    @Transactional
    public void reserveItemsUnsafe(Long skuId, int qty) {
        // multiple threads may run this concurrently -> race on inMemoryStock
        int available = inMemoryStock.getOrDefault(skuId, 0);
        if (available < qty) {
            log.error("Out of order");
        }
        inMemoryStock.put(skuId, available - qty); // race
        // persist DB updates too...
    }

    // Option A: thread-safe in-memory
    private final ConcurrentHashMap<Long, AtomicInteger> inMemoryStockSafe = new ConcurrentHashMap<>();
    @Transactional
    public void reserveItemsSafe(Long skuId, int qty) {
        inMemoryStockSafe.computeIfAbsent(skuId, k -> new AtomicInteger(0))
                .getAndUpdate(curr -> {
                    if (curr < qty) {
                        log.error("Out of order");
                    }
                    return curr - qty;
                });
        // DB transaction for persistence follows...
    }

    // or Option B:
    /*
    or better:
    • Use DB atomic operations (UPDATE ... WHERE stock >= ?) and check affected rows to ensure no race across instances.
    • Or use Redis atomic ops (see Redis section).

    Why safe: ConcurrentHashMap + AtomicInteger provide concurrent updates; DB/Redis operations are single-source-of-truth and 
        can be used atomically across instances.
     */
}
