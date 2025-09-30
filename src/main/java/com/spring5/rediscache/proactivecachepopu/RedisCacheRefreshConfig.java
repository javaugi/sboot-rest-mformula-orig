/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.rediscache.proactivecachepopu;

import com.spring5.RedisBaseConfig;
import com.spring5.entity.Product;
import java.util.Arrays;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/*
Key Benefits of This Approach
    Scalability: Redis can handle high throughput and scales horizontally
    Persistence: Survives application restarts unlike in-memory maps
    Distributed: Works across multiple instances of your service
    TTL Support: Automatic expiration of keys to prevent storage bloat
    Atomic Operations: Redis provides atomic operations for sequence numbers
Production Considerations
    Redis Cluster: For high availability, use Redis Cluster
    Monitoring: Monitor Redis memory usage and performance
    Backup: Configure Redis persistence appropriately
    Partitioning: Consider how to partition your data if needed
    Error Handling: Implement proper error handling for Redis operations
This implementation provides a robust solution for both idempotency in your REST API and ordered
    processing of Kafka messages using Redis as the coordination layer.
 */
@Configuration
public class RedisCacheRefreshConfig extends RedisBaseConfig {

    /* Key components
  1. Redis Configuration                          - RediConfig
  2. Redis-backed Idempotency Service             - RedibackedIdempotencyService
  3. Updated Idempotency Filter with Redis        - RedibackedIdempotencyFilter
  4. Kafka Event Ordering with Redis              - KafkaOrderedConsumer
  5. Processing Out-of-Order Events               - ScheduledOutOfOrderEventProcessor
  6. Kafka Producer with Sequence Tracking        - KafkaOrderedProducerWithSequenceTracking
     */

    public static final String REDIS_TPL_PRODUCT = "REDIS_TPL_PRODUCT";

    @Bean(name = REDIS_TPL_PRODUCT)
    public RedisTemplate<String, Product> redisObjTemplate() {
        RedisTemplate<String, Product> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());

        Jackson2JsonRedisSerializer<Product> serializer
                = new Jackson2JsonRedisSerializer<>(Product.class);
        template.setValueSerializer(serializer);
        return template;
    }

    @Bean
    public RedisCacheManager usCacheManager(RedisConnectionFactory usFactory) {
        return RedisCacheManager.builder(usFactory).build();
    }

    @Bean
    public RedisCacheManager euCacheManager(RedisConnectionFactory euFactory) {
        return RedisCacheManager.builder(euFactory).build();
    }

    @Bean("regionKeyGenerator")
    public KeyGenerator regionKeyGenerator() {
        // return (target, method, params) -> {
        return (target, method, params) -> {
            String region = CacheRegionContextHolder.getCurrentRegion(); // e.g., from ThreadLocal
            return region + ":" + Arrays.toString(params);
        };
    }
    //           return Objects.hash(method, Arrays.deepHashCode(params));
}

/*
Integrating Redis Cache with Kafka for Idempotency and Event Ordering
To replace the in-memory ConcurrentHashMap with Redis for your idempotency keys and Kafka event ordering, here's how to implement it properly in a production environment.

1. Redis Configuration
First, set up Redis configuration in your Spring Boot application:

java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }
}
2. Redis-backed Idempotency Service
Create a service to handle idempotency with Redis:

java
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

@Service
public class IdempotencyService {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String IDEMPOTENCY_PREFIX = "idempotency:";
    private static final long IDEMPOTENCY_KEY_TTL = 24; // hours

    public IdempotencyService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean isDuplicate(String idempotencyKey) {
        return redisTemplate.hasKey(IDEMPOTENCY_PREFIX + idempotencyKey);
    }

    public void storeResponse(String idempotencyKey, Object response) {
        redisTemplate.opsForValue().set(
            IDEMPOTENCY_PREFIX + idempotencyKey,
            response,
            IDEMPOTENCY_KEY_TTL,
            TimeUnit.HOURS
        );
    }

    public Object getResponse(String idempotencyKey) {
        return redisTemplate.opsForValue().get(IDEMPOTENCY_PREFIX + idempotencyKey);
    }
}
3. Updated Idempotency Filter with Redis
Modify the filter to use Redis instead of the in-memory map:

java
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class IdempotencyFilter extends OncePerRequestFilter {

    private static final String IDEMPOTENCY_KEY_HEADER = "Idempotency-Key";
    private final IdempotencyService idempotencyService;

    public IdempotencyFilter(IdempotencyService idempotencyService) {
        this.idempotencyService = idempotencyService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain filterChain)
        throws ServletException, IOException {

        if ("POST".equalsIgnoreCase(request.getMethod()) ||
            "PUT".equalsIgnoreCase(request.getMethod())) {

            String idempotencyKey = request.getHeader(IDEMPOTENCY_KEY_HEADER);

            if (idempotencyKey != null && !idempotencyKey.isEmpty()) {
                if (idempotencyService.isDuplicate(idempotencyKey)) {
                    Object cachedResponse = idempotencyService.getResponse(idempotencyKey);
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().write(cachedResponse.toString());
                    return;
                }

                ContentCachingResponseWrapper responseWrapper =
                    new ContentCachingResponseWrapper(response);

                try {
                    filterChain.doFilter(request, responseWrapper);

                    if (responseWrapper.getStatus() >= 200 &&
                        responseWrapper.getStatus() < 300) {
                        String responseBody = responseWrapper.toString();
                        idempotencyService.storeResponse(idempotencyKey, responseBody);
                    }

                    responseWrapper.copyBodyToResponse();
                } finally {
                    responseWrapper.copyBodyToResponse();
                }
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
4. Kafka Event Ordering with Redis
For Kafka message ordering, you can use Redis to track sequence numbers or timestamps:

java
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.RedisTemplate;

@Service
public class KafkaOrderedConsumer {

    private static final String SEQUENCE_PREFIX = "kafka:sequence:";
    private final RedisTemplate<String, Long> redisTemplate;

    public KafkaOrderedConsumer(RedisTemplate<String, Long> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @KafkaListener(topics = "ordered-events")
    public void processOrderedEvent(OrderedEvent event) {
        String sequenceKey = SEQUENCE_PREFIX + event.getEntityId();

        // Get last processed sequence number from Redis
        Long lastSequence = redisTemplate.opsForValue().get(sequenceKey);
        lastSequence = lastSequence == null ? 0L : lastSequence;

        // Check if this event is in order
        if (event.getSequenceNumber() == lastSequence + 1) {
            // Process the event
            processEvent(event);

            // Update the sequence number in Redis
            redisTemplate.opsForValue().set(sequenceKey, event.getSequenceNumber());
        } else if (event.getSequenceNumber() > lastSequence + 1) {
            // Store in Redis for later processing (out-of-order)
            redisTemplate.opsForList().rightPush(
                "kafka:out-of-order:" + event.getEntityId(),
                event
            );
        }
        // Else (duplicate or old event) - ignore
    }

    private void processEvent(OrderedEvent event) {
        // Your business logic here
    }
}
5. Processing Out-of-Order Events
Add a scheduled task to process out-of-order events:

java
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OutOfOrderEventProcessor {

    private final RedisTemplate<String, Object> redisTemplate;
    private final KafkaOrderedConsumer eventConsumer;

    public OutOfOrderEventProcessor(RedisTemplate<String, Object> redisTemplate,
                                  KafkaOrderedConsumer eventConsumer) {
        this.redisTemplate = redisTemplate;
        this.eventConsumer = eventConsumer;
    }

    @Scheduled(fixedRate = 5000) // Run every 5 seconds
    public void processOutOfOrderEvents() {
        // Get all entity IDs with out-of-order events
        Set<String> entityKeys = redisTemplate.keys("kafka:out-of-order:*");

        for (String key : entityKeys) {
            String entityId = key.substring("kafka:out-of-order:".length());
            String sequenceKey = "kafka:sequence:" + entityId;

            Long lastSequence = redisTemplate.opsForValue().get(sequenceKey);
            lastSequence = lastSequence == null ? 0L : lastSequence;

            // Peek at the next event without removing it
            OrderedEvent nextEvent = (OrderedEvent) redisTemplate.opsForList().index(key, 0);

            if (nextEvent != null && nextEvent.getSequenceNumber() == lastSequence + 1) {
                // Process the event
                eventConsumer.processOrderedEvent(nextEvent);

                // Remove the processed event
                redisTemplate.opsForList().leftPop(key);
            }
        }
    }
}
6. Kafka Producer with Sequence Tracking
For the producer side:

java
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaOrderedProducer {

    private final KafkaTemplate<String, OrderedEvent> kafkaTemplate;
    private final RedisTemplate<String, Long> redisTemplate;

    public KafkaOrderedProducer(KafkaTemplate<String, OrderedEvent> kafkaTemplate,
                              RedisTemplate<String, Long> redisTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.redisTemplate = redisTemplate;
    }

    public void sendOrderedEvent(String entityId, OrderedEvent event) {
        // Get and increment sequence number atomically
        String sequenceKey = "kafka:producer:sequence:" + entityId;
        Long sequenceNumber = redisTemplate.opsForValue().increment(sequenceKey);

        // Set the sequence number in the event
        event.setSequenceNumber(sequenceNumber);
        event.setEntityId(entityId);

        // Send to Kafka with entityId as key to ensure ordering
        kafkaTemplate.send("ordered-events", entityId, event);
    }
}
7. Event Class Example
java
public class OrderedEvent {
    private String entityId;
    private long sequenceNumber;
    private String eventType;
    private Map<String, Object> payload;

    // Constructors, getters, and setters
    public OrderedEvent() {}

    public OrderedEvent(String entityId, long sequenceNumber, String eventType, Map<String, Object> payload) {
        this.entityId = entityId;
        this.sequenceNumber = sequenceNumber;
        this.eventType = eventType;
        this.payload = payload;
    }

    // Getters and setters
    public String getEntityId() { return entityId; }
    public void setEntityId(String entityId) { this.entityId = entityId; }
    public long getSequenceNumber() { return sequenceNumber; }
    public void setSequenceNumber(long sequenceNumber) { this.sequenceNumber = sequenceNumber; }
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public Map<String, Object> getPayload() { return payload; }
    public void setPayload(Map<String, Object> payload) { this.payload = payload; }
}
Key Benefits of This Approach
    Scalability: Redis can handle high throughput and scales horizontally
    Persistence: Survives application restarts unlike in-memory maps
    Distributed: Works across multiple instances of your service
    TTL Support: Automatic expiration of keys to prevent storage bloat
    Atomic Operations: Redis provides atomic operations for sequence numbers
Production Considerations
    Redis Cluster: For high availability, use Redis Cluster
    Monitoring: Monitor Redis memory usage and performance
    Backup: Configure Redis persistence appropriately
    Partitioning: Consider how to partition your data if needed
    Error Handling: Implement proper error handling for Redis operations
This implementation provides a robust solution for both idempotency in your REST API and ordered processing of Kafka messages using Redis as the coordination layer.
 */
