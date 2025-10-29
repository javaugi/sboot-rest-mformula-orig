/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.actuator.prometheus;

import com.spring5.mongodb.Customer;
import com.spring5.mongodb.dto.PagedCustomers;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class CustomerService {

    private final MeterRegistry meterRegistry;
    private final Timer getUsersPageTimer;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;

    public CustomerService(MeterRegistry meterRegistry, R2dbcEntityTemplate r2dbcEntityTemplate) {
        this.meterRegistry = meterRegistry;
        this.r2dbcEntityTemplate = r2dbcEntityTemplate;
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
