/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.wmart.trans;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
//import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

@Service
@Slf4j
public class StoreTransactionProducer {

    private static final String TRANSACTIONS_TOPIC = "store-transactions";

    private final KafkaTemplate<String, StoreTransaction> kafkaTemplate;
    private final MeterRegistry meterRegistry;
    private final Counter successCounter;
    private final Counter errorCounter;

    public StoreTransactionProducer(KafkaTemplate<String, StoreTransaction> kafkaTemplate,
        MeterRegistry meterRegistry) {
        this.kafkaTemplate = kafkaTemplate;
        this.meterRegistry = meterRegistry;

        // Metrics setup
        this.successCounter = Counter.builder("kafka.producer.success")
            .description("Successful message productions")
            .register(meterRegistry);
        this.errorCounter = Counter.builder("kafka.producer.errors")
            .description("Failed message productions")
            .register(meterRegistry);
    }

    @Async
    public CompletableFuture<SendResult<String, StoreTransaction>> sendTransaction(StoreTransaction transaction) {
        String storeId = transaction.getStoreId();

        // Use storeId as key to ensure partitioning by store
        CompletableFuture<SendResult<String, StoreTransaction>> future
            = kafkaTemplate.send(TRANSACTIONS_TOPIC, storeId, transaction);

        return future.toCompletableFuture()
            .thenApply(result -> {
                successCounter.increment();
                log.debug("Successfully sent transaction for store: {} to partition: {}",
                    storeId, result.getRecordMetadata().partition());
                return result;
            })
            .exceptionally(ex -> {
                errorCounter.increment();
                log.error("Failed to send transaction for store: {}", storeId, ex);
                throw new RuntimeException("Failed to send Kafka message", ex);
            });
    }

    // Batch sending for better throughput
    public void sendTransactionsBatch(List<StoreTransaction> transactions) {
        List<CompletableFuture<SendResult<String, StoreTransaction>>> futures = new ArrayList<>();

        for (StoreTransaction transaction : transactions) {
            futures.add(sendTransaction(transaction));
        }

        // Wait for all sends to complete
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
            .exceptionally(ex -> {
                log.error("Batch send partially failed", ex);
                return null;
            });
    }
}
