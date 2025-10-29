/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.actuator.prometheus;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.binder.MeterBinder;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Component;

/*
Recommended Approach
I'd recommend approach #2 or #3 as they:
    Properly handle reactive streams
    Include error tracking
    Add useful tags for filtering metrics
    Are explicit about what's being measured

The metrics will be available at /actuator/prometheus endpoint if you have Spring Boot Actuator configured.
 */

@Component
public class CustomerMetrics implements MeterBinder {

    private final CustomerService customerService;
    private MeterRegistry meterRegistry;

    public CustomerMetrics(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Override
    public void bindTo(MeterRegistry registry) {
        this.meterRegistry = registry;

        Timer.builder("customer.get.users.page")
                .description("Time taken to get paginated customers")
                .register(registry);
    }

    // You can expose methods to record metrics
    public void recordPageRequest(long duration, int page, int size, String status) {
        meterRegistry.timer("customer.get.users.page",
                "page", String.valueOf(page),
                "size", String.valueOf(size),
                "status", status)
                .record(duration, TimeUnit.MILLISECONDS);
    }
}
/*
Yes! You can incorporate Prometheus metrics using MeterRegistry to track the performance of your reactive method. Here are a few ways to do it:

1. Basic Timer Approach
java
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

@Service
public class CustomerService {
    
    private final MeterRegistry meterRegistry;
    private final Timer getUsersPageTimer;
    
    public CustomerService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.getUsersPageTimer = Timer.builder("customer.get.users.page")
                .description("Time taken to get paginated customers")
                .register(meterRegistry);
    }
    
    public Mono<PagedCustomers> getUsersPage(int page, int size) {
        return Mono.fromCallable(() -> getUsersPageTimer.record(() -> {
                    int pageIndex = Math.max(0, page - 1);
                    Query query = Query.empty()
                            .limit(size)
                            .offset((long) pageIndex * size)
                            .sort(Sort.by("id"));

                    Mono<List<Customer>> customersMono = r2dbcEntityTemplate.select(query, Customer.class).collectList();
                    Mono<Long> totalMono = r2dbcEntityTemplate.select(Customer.class).count();
                    
                    return Mono.zip(customersMono, totalMono)
                            .map(tuple -> new PagedCustomers(tuple.getT1(), tuple.getT2()));
                }))
                .flatMap(mono -> mono);
    }
}
2. Reactive-Friendly Approach with Timer.Sample
java
public Mono<PagedCustomers> getUsersPage(int page, int size) {
    Timer.Sample sample = Timer.start(meterRegistry);
    
    int pageIndex = Math.max(0, page - 1);
    Query query = Query.empty()
            .limit(size)
            .offset((long) pageIndex * size)
            .sort(Sort.by("id"));

    Mono<List<Customer>> customersMono = r2dbcEntityTemplate.select(query, Customer.class).collectList();
    Mono<Long> totalMono = r2dbcEntityTemplate.select(Customer.class).count();
    
    return Mono.zip(customersMono, totalMono)
            .map(tuple -> new PagedCustomers(tuple.getT1(), tuple.getT2()))
            .doOnSuccess(result -> {
                sample.stop(meterRegistry.timer("customer.get.users.page",
                    "status", "success",
                    "page", String.valueOf(page),
                    "size", String.valueOf(size)));
            })
            .doOnError(error -> {
                sample.stop(meterRegistry.timer("customer.get.users.page",
                    "status", "error",
                    "page", String.valueOf(page),
                    "size", String.valueOf(size)));
            });
}
3. More Comprehensive Approach with Additional Metrics
java
@Service
public class CustomerService {
    
    private final MeterRegistry meterRegistry;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final Counter successCounter;
    private final Counter errorCounter;
    
    public CustomerService(MeterRegistry meterRegistry, R2dbcEntityTemplate r2dbcEntityTemplate) {
        this.meterRegistry = meterRegistry;
        this.r2dbcEntityTemplate = r2dbcEntityTemplate;
        
        this.successCounter = Counter.builder("customer.get.users.page.success")
                .description("Number of successful customer page requests")
                .register(meterRegistry);
                
        this.errorCounter = Counter.builder("customer.get.users.page.error")
                .description("Number of failed customer page requests")
                .register(meterRegistry);
    }
    
    public Mono<PagedCustomers> getUsersPage(int page, int size) {
        Timer.Sample sample = Timer.start(meterRegistry);
        
        int pageIndex = Math.max(0, page - 1);
        Query query = Query.empty()
                .limit(size)
                .offset((long) pageIndex * size)
                .sort(Sort.by("id"));

        Mono<List<Customer>> customersMono = r2dbcEntityTemplate.select(query, Customer.class).collectList();
        Mono<Long> totalMono = r2dbcEntityTemplate.select(Customer.class).count();
        
        return Mono.zip(customersMono, totalMono)
                .map(tuple -> {
                    PagedCustomers result = new PagedCustomers(tuple.getT1(), tuple.getT2());
                    // Record additional metrics
                    meterRegistry.gauge("customer.page.size", tuple.getT1().size());
                    meterRegistry.gauge("customer.total.count", tuple.getT2());
                    return result;
                })
                .doOnSuccess(result -> {
                    sample.stop(Timer.builder("customer.get.users.page.duration")
                            .tag("status", "success")
                            .tag("page", String.valueOf(page))
                            .tag("size", String.valueOf(size))
                            .register(meterRegistry));
                    successCounter.increment();
                })
                .doOnError(error -> {
                    sample.stop(Timer.builder("customer.get.users.page.duration")
                            .tag("status", "error")
                            .tag("page", String.valueOf(page))
                            .tag("size", String.valueOf(size))
                            .tag("error_type", error.getClass().getSimpleName())
                            .register(meterRegistry));
                    errorCounter.increment();
                });
    }
}
4. Using @Timed Annotation (Simplest Approach)
If you have Micrometer's aspectj support, you can use the @Timed annotation:

java
import io.micrometer.core.annotation.Timed;

@Timed(value = "customer.get.users.page", description = "Time taken to get paginated customers")
public Mono<PagedCustomers> getUsersPage(int page, int size) {
    int pageIndex = Math.max(0, page - 1);
    Query query = Query.empty()
            .limit(size)
            .offset((long) pageIndex * size)
            .sort(Sort.by("id"));

    Mono<List<Customer>> customersMono = r2dbcEntityTemplate.select(query, Customer.class).collectList();
    Mono<Long> totalMono = r2dbcEntityTemplate.select(Customer.class).count();
    return Mono.zip(customersMono, totalMono)
            .map(tuple -> new PagedCustomers(tuple.getT1(), tuple.getT2()));
}
For this to work, you need to add @EnableAspectJAutoProxy to your configuration and include the aspectj dependency:

xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-aop</artifactId>
</dependency>
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-core</artifactId>
</dependency>
5. Custom MeterBinder (Most Flexible)
java
@Component
public class CustomerMetrics implements MeterBinder {
    
    private final CustomerService customerService;
    private MeterRegistry meterRegistry;
    
    public CustomerMetrics(CustomerService customerService) {
        this.customerService = customerService;
    }
    
    @Override
    public void bindTo(MeterRegistry registry) {
        this.meterRegistry = registry;
        
        Timer.builder("customer.get.users.page")
                .description("Time taken to get paginated customers")
                .register(registry);
    }
    
    // You can expose methods to record metrics
    public void recordPageRequest(long duration, int page, int size, String status) {
        meterRegistry.timer("customer.get.users.page",
                "page", String.valueOf(page),
                "size", String.valueOf(size),
                "status", status)
                .record(duration, TimeUnit.MILLISECONDS);
    }
}
Recommended Approach
I'd recommend approach #2 or #3 as they:

Properly handle reactive streams

Include error tracking

Add useful tags for filtering metrics

Are explicit about what's being measured

The metrics will be available at /actuator/prometheus endpoint if you have Spring Boot Actuator configured.

Remember to add these dependencies:

xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
*/
