/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.security.threadsafety;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring5.audit.AuditOrder;
import com.spring5.audit.OrderService;
import com.spring5.dto.ProjectDTO;
import com.spring5.service.ProjectService;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
@RestController
@RequestMapping("/api/threadsafety")
@Scope("prototype")
public class ThreadSafeController {

    // MUTABLE shared state — NOT THREAD SAFE
    private Long lastRequest; // ❌ NOT thread-safe, shared across all requests
    private String lastOrderJson; // BAD

    private final ProjectService projectService;
    private final OrderService orderService;

    @GetMapping("/order/{id}")
    public ProjectDTO getProjectbyId(@PathVariable Long id) {
        lastRequest = id; // race condition
        return projectService.findById(id);
    }

    @PostMapping("/orderunsafe")
    public ResponseEntity<?> createOrderUnsafe(@RequestBody AuditOrder order) {
        try {
            lastOrderJson = new ObjectMapper().writeValueAsString(order); // race
            Long id = orderService.createOrder(order);
            return ResponseEntity.status(201).body(Map.of("id", id));
        } catch (JsonProcessingException ex) {

        }

        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/ordersafe")
    public ResponseEntity<?> createOrderSafe(@Valid @RequestBody AuditOrder req) {
        // per-request data is local variable — thread-local by stack
        Long id = orderService.createOrder(req);
        return ResponseEntity.status(201).body(Map.of("id", id));
    }
}
