/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.audit;

/**
 *
 * @author javau
 */
public class AuditMetricsConfig {
    
}


/*
Migrating Hibernate Audit Logging to a Dedicated Microservice
Here's a comprehensive approach to transition from Hibernate interceptors to a dedicated audit logging microservice in your Spring Boot application:

Current Architecture Analysis
Your current setup likely uses Hibernate's EmptyInterceptor or EntityNameResolver to capture CRUD operations. This creates tight coupling between your business logic and audit logging.

Solution Architecture
1. Event-Driven Audit Service Design
text
[Your App] --Audit Events--> [Kafka/RabbitMQ] --> [Audit Service] --> [Audit DB]
2. Implementation Steps
Step 1: Create Audit Event DTO
java
public class AuditEvent {
    private UUID eventId;
    private AuditAction action; // CREATE, UPDATE, DELETE, READ
    private String entityType;
    private String entityId;
    private String userId;
    private Instant timestamp;
    private Map<String, Object> oldValues;
    private Map<String, Object> newValues;
    private String sourceApplication;
    
    // Constructors, getters, setters
}

public enum AuditAction {
    CREATE, UPDATE, DELETE, READ
}
Step 2: Replace Hibernate Interceptor with Spring AOP
java
@Aspect
@Component
public class AuditLoggingAspect {
    
    private final AuditEventPublisher eventPublisher;
    
    @Autowired
    public AuditLoggingAspect(AuditEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }
    
    @AfterReturning(
        pointcut = "execution(* org.springframework.data.repository.Repository+.save*(..)) && args(entity)",
        returning = "result")
    public void logCreateOrUpdate(Object entity, Object result) {
        if (entity != null) {
            AuditAction action = entity instanceof Persistable && ((Persistable<?>) entity).isNew() 
                ? AuditAction.CREATE 
                : AuditAction.UPDATE;
                
            eventPublisher.publishEvent(entity, action);
        }
    }
    
    @AfterReturning(
        pointcut = "execution(* org.springframework.data.repository.Repository+.delete*(..)) && args(entity)")
    public void logDelete(Object entity) {
        if (entity != null) {
            eventPublisher.publishEvent(entity, AuditAction.DELETE);
        }
    }
}
Step 3: Create Event Publisher Service
java
@Service
public class AuditEventPublisher {
    
    private final KafkaTemplate<String, AuditEvent> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final SecurityContext securityContext;
    
    public void publishEvent(Object entity, AuditAction action) {
        AuditEvent event = createAuditEvent(entity, action);
        kafkaTemplate.send("audit-events", event.getEntityType() + "-" + event.getEntityId(), event);
    }
    
    private AuditEvent createAuditEvent(Object entity, AuditAction action) {
        try {
            String entityId = extractId(entity);
            Map<String, Object> currentState = objectMapper.convertValue(entity, new TypeReference<>() {});
            
            return new AuditEvent(
                UUID.randomUUID(),
                action,
                entity.getClass().getSimpleName(),
                entityId,
                securityContext.getCurrentUserId(),
                Instant.now(),
                action == AuditAction.UPDATE ? fetchPreviousState(entity) : null,
                currentState,
                "vehicle-config-service"
            );
        } catch (Exception e) {
            log.error("Failed to create audit event", e);
            throw new AuditException("Audit event creation failed", e);
        }
    }
    
    private String extractId(Object entity) {
        // Implementation using reflection or entity-specific ID access
    }
    
    private Map<String, Object> fetchPreviousState(Object entity) {
        // For UPDATE actions, fetch previous state from DB if needed
    }
}
Step 4: Configure Kafka Producer
java
@Configuration
public class KafkaConfig {
    
    @Bean
    public ProducerFactory<String, AuditEvent> auditEventProducerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka-broker:9092");
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        config.put(ProducerConfig.ACKS_CONFIG, "all");
        config.put(ProducerConfig.RETRIES_CONFIG, 3);
        return new DefaultKafkaProducerFactory<>(config);
    }
    
    @Bean
    public KafkaTemplate<String, AuditEvent> auditKafkaTemplate() {
        return new KafkaTemplate<>(auditEventProducerFactory());
    }
}
3. Audit Microservice Implementation
Audit Service Controller
java
@RestController
@RequestMapping("/api/audit")
public class AuditController {
    
    private final AuditService auditService;
    
    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void handleAuditEvent(@RequestBody AuditEvent event) {
        auditService.processEvent(event);
    }
    
    @GetMapping("/entity/{type}/{id}")
    public List<AuditEvent> getEntityHistory(
            @PathVariable String type,
            @PathVariable String id) {
        return auditService.getEntityHistory(type, id);
    }
}
Kafka Consumer in Audit Service
java
@Service
public class AuditEventConsumer {
    
    private final AuditService auditService;
    
    @KafkaListener(topics = "audit-events", groupId = "audit-service-group")
    public void consume(AuditEvent event) {
        auditService.processEvent(event);
    }
}
Audit Service Implementation
java
@Service
public class AuditService {
    
    private final AuditRepository repository;
    
    @Transactional
    public void processEvent(AuditEvent event) {
        // Transform and store in dedicated audit database
        AuditEntry entry = convertToEntry(event);
        repository.save(entry);
    }
    
    private AuditEntry convertToEntry(AuditEvent event) {
        // Conversion logic
    }
    
    public List<AuditEvent> getEntityHistory(String entityType, String entityId) {
        return repository.findByEntityTypeAndEntityIdOrderByTimestampDesc(entityType, entityId)
            .stream()
            .map(this::convertToEvent)
            .collect(Collectors.toList());
    }
}
Migration Strategy
Phase 1: Dual Writing

Keep existing Hibernate interceptor

Add new event publishing mechanism

Verify both systems produce identical logs

Phase 2: Shadow Mode

Route read requests to new audit service

Compare results with old system

Monitor for discrepancies

Phase 3: Cutover

Remove Hibernate interceptor

Fully switch to event-driven architecture

Implement fallback mechanism

Error Handling & Reliability
java
@Configuration
public class KafkaErrorConfig {
    
    @Bean
    public DeadLetterPublishingRecoverer dlqRecoverer(
            KafkaOperations<String, Object> template) {
        return new DeadLetterPublishingRecoverer(template);
    }
    
    @Bean
    public DefaultErrorHandler errorHandler(
            DeadLetterPublishingRecoverer dlqRecoverer) {
        return new DefaultErrorHandler(
            dlqRecoverer,
            new FixedBackOff(1000L, 3L));
    }
}
Performance Considerations
Event Batching:

java
@Bean
public ProducerFactory<String, AuditEvent> batchingProducerFactory() {
    Map<String, Object> config = new HashMap<>();
    // Existing config plus:
    config.put(ProducerConfig.LINGER_MS_CONFIG, 100); // 100ms batching window
    config.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384); // 16KB batch size
    return new DefaultKafkaProducerFactory<>(config);
}
Asynchronous Processing:

java
@Async("auditTaskExecutor")
public void publishEvent(Object entity, AuditAction action) {
    // Existing implementation
}

@Bean(name = "auditTaskExecutor")
public TaskExecutor auditTaskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(5);
    executor.setMaxPoolSize(10);
    executor.setQueueCapacity(100);
    executor.setThreadNamePrefix("audit-publisher-");
    return executor;
}
Monitoring
java
@Configuration
public class AuditMetricsConfig {
    
    @Bean
    public MeterBinder auditMetrics(AuditRepository repository) {
        return registry -> {
            Gauge.builder("audit.events.count", repository::count)
                .description("Total audit events in system")
                .register(registry);
            
            Counter.builder("audit.events.received")
                .description("Incoming audit events")
                .tag("source", "kafka")
                .register(registry);
        };
    }
}
This approach provides a scalable, decoupled audit logging solution that maintains data consistency while improving system performance and maintainability
*/

/*
How can this method work if the database record has been updated already  private Map<String, Object> fetchPreviousState(Object entity) {
        // For UPDATE actions, fetch previous state from DB if needed
    }
Handling Previous State Fetching for Audit Logs
When implementing audit logging for UPDATE operations, fetching the previous state after the database record has already been updated presents a challenge. Here are several robust approaches to solve this problem:

1. Pre-Update State Capture (Recommended)
Modify your AOP aspect to capture state before the update:

java
@Aspect
@Component
public class AuditLoggingAspect {
    
    private final ThreadLocal<Map<String, Object>> previousState = new ThreadLocal<>();
    
    @Before("execution(* org.springframework.data.repository.Repository+.save*(..)) && args(entity)")
    public void captureBeforeState(Object entity) {
        if (entity != null && !isNewEntity(entity)) {
            previousState.set(fetchCurrentStateFromDb(entity));
        }
    }
    
    @AfterReturning(
        pointcut = "execution(* org.springframework.data.repository.Repository+.save*(..)) && args(entity)",
        returning = "result")
    public void logCreateOrUpdate(Object entity, Object result) {
        if (entity != null) {
            AuditAction action = isNewEntity(entity) 
                ? AuditAction.CREATE 
                : AuditAction.UPDATE;
                
            Map<String, Object> oldValues = action == AuditAction.UPDATE 
                ? previousState.get() 
                : null;
                
            eventPublisher.publishEvent(entity, action, oldValues);
            previousState.remove();
        }
    }
    
    private boolean isNewEntity(Object entity) {
        return entity instanceof Persistable && ((Persistable<?>) entity).isNew();
    }
    
    private Map<String, Object> fetchCurrentStateFromDb(Object entity) {
        Object id = entityManager.getEntityManagerFactory()
            .getPersistenceUnitUtil()
            .getIdentifier(entity);
        
        Object dbEntity = entityManager.find(entity.getClass(), id);
        return objectMapper.convertValue(dbEntity, new TypeReference<>() {});
    }
}
2. Hibernate Envers (Alternative)
If you're using Hibernate, consider using Hibernate Envers which automatically maintains audit tables:

java
@Entity
@Audited
public class YourEntity {
    // Your entity fields
}

// Then query audit history:
AuditReader reader = AuditReaderFactory.get(entityManager);
List<Number> revisions = reader.getRevisions(YourEntity.class, entityId);
YourEntity oldVersion = reader.find(YourEntity.class, entityId, revisions.get(revisions.size()-2));
3. Event Sourcing Pattern
For critical systems where complete audit history is essential:

java
public class YourEntity {
    @Version
    private Long version;
    
    @Transient
    private Map<String, Object> previousState;
    
    @PreUpdate
    public void captureState() {
        this.previousState = // capture current state from DB
    }
    
    @PostUpdate
    public void publishEvent() {
        if (previousState != null) {
            auditService.publishUpdateEvent(this, previousState);
        }
    }
}
4. Database Triggers (When All Else Fails)
As a last resort, you can use database triggers to capture before-state:

sql
CREATE TRIGGER capture_before_update
BEFORE UPDATE ON your_table
FOR EACH ROW
BEGIN
    INSERT INTO audit_log (entity_id, old_values, changed_at)
    VALUES (OLD.id, JSON_OBJECT(
        'field1', OLD.field1,
        'field2', OLD.field2,
        -- etc
    ), NOW());
END;
Best Practice Implementation
Here's a complete solution combining several approaches:

java
public class AuditStateCapturer {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    private final ObjectMapper objectMapper;
    
    public Map<String, Object> capturePreUpdateState(Object entity) {
        if (entity == null) return null;
        
        Object id = entityManager.getEntityManagerFactory()
            .getPersistenceUnitUtil()
            .getIdentifier(entity);
        
        if (id == null) return null; // New entity
        
        Object dbEntity = entityManager.find(entity.getClass(), id);
        if (dbEntity == null) return null;
        
        return objectMapper.convertValue(dbEntity, new TypeReference<>() {});
    }
}

@Aspect
@Component
@RequiredArgsConstructor
public class AuditAspect {
    
    private final AuditStateCapturer stateCapturer;
    private final AuditEventPublisher eventPublisher;
    
    @Around("@annotation(com.yourpackage.Auditable)")
    public Object auditMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        if (args.length == 0) return joinPoint.proceed();
        
        Object entity = args[0];
        Map<String, Object> oldState = stateCapturer.capturePreUpdateState(entity);
        
        Object result = joinPoint.proceed();
        
        if (entity != null) {
            AuditAction action = oldState == null ? AuditAction.CREATE : AuditAction.UPDATE;
            eventPublisher.publishEvent(entity, action, oldState);
        }
        
        return result;
    }
}

// Usage on service methods:
@Service
public class YourService {
    
    @Auditable
    public YourEntity updateEntity(YourEntity entity) {
        // your update logic
    }
}
Key Considerations
Performance Impact: Fetching previous state adds database overhead. Consider:

Caching frequently accessed entities

Batching audit events

Async processing

Transaction Boundaries: Ensure state capture happens in the same transaction

Error Handling: Implement proper error recovery for failed state captures

Concurrency: Handle cases where entities are modified between state capture and update

This approach gives you complete, accurate audit trails while maintaining good performance characteristics.
*/

/*
Transaction Management in Audit Logging
Yes, you're absolutely correct to focus on transaction boundaries. For reliable audit logging, the state capture and the actual database update must occur within the same transaction. Here's how to properly handle this:

Transactional Aspect Implementation
1. Annotate the Aspect as @Transactional
java
@Aspect
@Component
@Transactional
public class AuditLoggingAspect {
    // ...
}
Important Note: This makes the entire aspect transactional, which might not be ideal as it could lead to unintended transaction propagation.

2. Better Approach: Join Existing Transaction
java
@Aspect
@Component
public class AuditLoggingAspect {
    
    @Autowired
    private EntityManager entityManager;
    
    @Before("execution(* com.yourpackage..*.save*(..)) && args(entity)")
    public void captureBeforeState(Object entity) {
        if (entity != null && !isNewEntity(entity)) {
            // This will automatically participate in the current transaction
            Object id = entityManager.getEntityManagerFactory()
                .getPersistenceUnitUtil()
                .getIdentifier(entity);
            Object dbEntity = entityManager.find(entity.getClass(), id);
            // Store previous state in thread-local
        }
    }
}
3. Best Practice: Use Transaction-Synchronized Callbacks
java
@Aspect
@Component
public class AuditLoggingAspect {
    
    @Autowired
    private PlatformTransactionManager transactionManager;
    
    @Before("execution(* com.yourpackage..*.save*(..)) && args(entity)")
    public void captureBeforeState(Object entity) {
        TransactionSynchronizationManager.registerSynchronization(
            new TransactionSynchronization() {
                @Override
                public void beforeCommit(boolean readOnly) {
                    // This executes right before transaction commit
                    // Capture previous state here
                }
            }
        );
    }
}
Complete Transaction-Aware Solution
Here's a robust implementation that ensures proper transaction handling:

java
@Aspect
@Component
@RequiredArgsConstructor
public class AuditLoggingAspect {
    
    private final EntityManager entityManager;
    private final AuditEventPublisher eventPublisher;
    private final ThreadLocal<Map<Object, Map<String, Object>>> previousStates = new ThreadLocal<>();
    
    @Before("execution(* org.springframework.data.repository.Repository+.save*(..)) && args(entity)")
    public void captureBeforeState(Object entity) {
        if (entity == null || isNewEntity(entity)) return;
        
        TransactionSynchronizationManager.registerSynchronization(
            new TransactionSynchronizationAdapter() {
                @Override
                public void beforeCommit(boolean readOnly) {
                    Map<Object, Map<String, Object>> stateMap = previousStates.get();
                    if (stateMap == null) {
                        stateMap = new HashMap<>();
                        previousStates.set(stateMap);
                    }
                    
                    // Fetch current DB state within the transaction
                    Object id = entityManager.getEntityManagerFactory()
                        .getPersistenceUnitUtil()
                        .getIdentifier(entity);
                    Object dbEntity = entityManager.find(entity.getClass(), id);
                    
                    stateMap.put(entity, convertToMap(dbEntity));
                }
            }
        );
    }
    
    @AfterReturning(
        pointcut = "execution(* org.springframework.data.repository.Repository+.save*(..)) && args(entity)",
        returning = "result")
    public void logCreateOrUpdate(Object entity, Object result) {
        if (entity == null) return;
        
        try {
            AuditAction action = isNewEntity(entity) 
                ? AuditAction.CREATE 
                : AuditAction.UPDATE;
            
            Map<String, Object> oldValues = action == AuditAction.UPDATE 
                ? previousStates.get().get(entity) 
                : null;
            
            eventPublisher.publishEvent(entity, action, oldValues);
        } finally {
            previousStates.remove();
        }
    }
    
    private boolean isNewEntity(Object entity) {
        return entity instanceof Persistable && ((Persistable<?>) entity).isNew();
    }
    
    private Map<String, Object> convertToMap(Object entity) {
        // Implementation using reflection or ObjectMapper
    }
}
Transaction Propagation Considerations
REQUIRED (Default):

If a transaction exists, joins it

Creates new one if none exists

REQUIRES_NEW:

Always creates a new transaction

Suspends existing transaction if any

MANDATORY:

Must have an existing transaction

Throws exception if none exists

For audit logging, you typically want REQUIRED behavior to participate in the business transaction.

Handling Transaction Rollbacks
java
@Aspect
@Component
public class AuditLoggingAspect {
    
    // ...
    
    @AfterThrowing(
        pointcut = "execution(* org.springframework.data.repository.Repository+.save*(..))",
        throwing = "ex")
    public void handleRollback(Exception ex) {
        previousStates.remove();
    }
}
Performance Optimization
Entity State Cache:

java
@Aspect
@Component
public class AuditLoggingAspect {
    
    private final Cache<Object, Map<String, Object>> stateCache = 
        Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build();
    
    // In beforeCommit:
    stateCache.put(entity, convertToMap(dbEntity));
    
    // In logCreateOrUpdate:
    Map<String, Object> oldValues = stateCache.getIfPresent(entity);
}
Batch Event Publishing:

java
@Transactional(propagation = Propagation.NESTED)
public void publishEvents(List<AuditEvent> events) {
    // Batch insert to audit log
}
Spring Boot Configuration
Ensure proper transaction management in your configuration:

java
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories
public class PersistenceConfig {
    
    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }
}
This implementation ensures that:

State capture happens in the same transaction as the business operation

Audit events are only published if the transaction commits

Resources are properly cleaned up on rollback

The solution is performant even with high transaction volumes
*/