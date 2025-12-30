/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.security.threadsafety;

/*
5) End-to-end patterns to prevent thread-safety propagation
    1. Stateless services: Keep controllers/services singleton but stateless. Store per-request data in method scope.
    2. Single source of truth: Use DB or Redis for shared state; avoid multiple in-memory caches across instances unless carefully synced.
    3. Atomic operations: Prefer atomic DB/Redis operations over read-then-write sequences in app memory.
    4. Idempotency & dedupe: Assign event IDs and store processed IDs (Redis SETNX or DB table with unique constraint).
    5. Outbox + Consumer side dedupe: Use Outbox pattern to guarantee publish. Consumers should use dedupe before applying side effects.
    6. Use thread-safe collections: ConcurrentHashMap, CopyOnWriteArrayList, AtomicLong, LongAdder.
    7. Avoid ThreadLocal in pooling environments: ThreadLocal can leak when threads are reused (e.g., servlet containers, executor pools). 
        If used, clear in finally.
    8. Reactive caution: In WebFlux/Reactive, do not assume a single thread; avoid ThreadLocal and mutable shared state.
    9. Testing: Use integration tests and concurrency tests (e.g., run multiple concurrent requests) to reproduce races.
    10. Observability: Trace messages across HTTP/Kafka/Redis (OpenTelemetry), and surface metrics for concurrency issues (thread pool 
        saturation, queue lengths, Kafka consumer lag, Redis command latencies).
 */
public class ThreadSafePropogationEndToEnd {
    /*
    6) Example: full small end-to-end safe flow (code snippets + explanations)
Create order (Spring Boot Controller -> Service -> Outbox -> Kafka)
    • Controller is stateless.
    • Service writes orders and outbox in one DB transaction.
    • Outbox publisher picks up PENDING events and publishes via thread-safe KafkaTemplate.
    • Consumer marks dedupe key in Redis via SETNX.
Key pieces:
Service writes outbox (transactional, but thread-isolated)
    
    


@Service
public class OrderService {
    @Transactional
    public OrderDto createOrder(OrderRequest req) {
        OrderEntity order = orderRepo.save(  );
        OutboxEvent ev = new OutboxEvent("orders", order.getId().toString(), toJson(order));
        outboxRepo.save(ev); // same tx as order
        return toDto(order);
    }
}
Why safe: Transactionality ensures DB atomicity; no shared mutable state inside the service.


    
    OutboxPublisher (runs in background; careful about concurrency)
@Component
public class OutboxPublisher {
    private final OutboxRepository outboxRepo;
    private final KafkaTemplate<String,String> kafka;
    private final ObjectMapper mapper;

    @Scheduled(fixedDelayString = "${outbox.poll-ms:2000}")
    public void pollAndPublish() {
        List<OutboxEvent> pending = outboxRepo.findTop100ByStatus(OutboxStatus.PENDING);
        for (OutboxEvent e : pending) {
            // idempotency: use DB update with WHERE status = PENDING to claim row
            if (claimOutboxRow(e.getId())) {
                try {
                    kafka.send(e.getTopic(), e.getAggregateId(), e.getPayload()).get(5, TimeUnit.SECONDS);
                    markSent(e.getId());
                } catch (Exception ex) {
                    // log; leave for retry
                }
            }
        }
    }
    private boolean claimOutboxRow(Long id) {
        // Use a repository method that does UPDATE outbox SET status='IN_FLIGHT' WHERE id = :id AND status='PENDING'
        return outboxRepo.tryClaim(id) == 1;
    }
}
Why safe: Claiming pattern avoids multiple publishers publishing same event; KafkaTemplate is thread-safe.
    
    
    Consumer + Redis dedupe + atomic processing
@KafkaListener(topics = "orders")
public void onOrder(String payload) {
    String eventId = extractEventId(payload);
    Boolean first = redisTemplate.opsForValue().setIfAbsent("processed:"+eventId, "1", Duration.ofDays(1));
    if (!Boolean.TRUE.equals(first)) return; // already processed

    // Use atomic Redis op to reserve inventory
    Boolean reserved = redisScriptExecutor.executeReserveScript("stock:sku:123", 1);
    if (!reserved) {
        // publish failure or compensate
    }
    // persist to DB etc.
}
Why safe: setIfAbsent is atomic dedupe; Lua script ensures inventory decrement is atomic.
    
    
    7) Debugging & Monitoring Tips (practical)
    • Thread dumps: Collect thread dumps (jstack) when you suspect deadlock or thread starvation.
    • Heap & GC logs: Check GC pauses that may simulate "thread freezing".
    • Instrument Kafka: monitor consumer group lag, throughput, and record processing times.
    • Redis slowlog & command stats: find hot keys and expensive Lua scripts.
    • Request tracing: use distributed tracing (OpenTelemetry / Jaeger) to trace one request across HTTP → Kafka → consumer → Redis.
    • Load tests: simulate concurrency (k6, Gatling) focusing on hotspots (reserve inventory endpoint).
    • Concurrency unit tests: use @RepeatedTest with parallel execution or tools like jcstress for low-level concurrency testing.

8) Quick Interview Q&A (summary cheatsheet)
    • Q: Are Spring controllers thread-safe by default?
A: Controllers are singletons — only thread-safe if stateless.
    • Q: Does @Transactional provide thread-safety?
A: No — it provides DB transaction boundaries for each thread, not mutual exclusion.
    • Q: How to safely decrement inventory across instances?
A: Use DB atomic UPDATE ... WHERE stock >= ? or Redis atomic DECRBY / Lua script or distributed lock.
    • Q: How to prevent duplicate Kafka processing?
A: Use idempotency keys with a dedupe store (DB unique constraint or Redis SETNX) and/or Kafka exactly-once semantics.
    • Q: Why avoid ThreadLocal in servlet containers?
A: Thread pools reuse threads — ThreadLocal may leak across requests unless cleared.
    
    
    UPDATE products
SET stock = stock - ?
WHERE product_id = ? AND stock >= ?;
    
    
BEGIN TRANSACTION;
-- Lock the specific row for update (syntax may vary by DB, e.g., WITH (UPDLOCK) in SQL Server)
SELECT stock INTO @current_stock FROM products WHERE product_id = 123 FOR UPDATE;

-- Check stock condition within your application logic
-- If @current_stock >= requested_quantity, proceed with UPDATE
UPDATE products SET stock = stock - requested_quantity WHERE product_id = 123;
INSERT INTO orders (...) VALUES (...);

COMMIT;    

     */

}
