/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.wmart.trans;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class StoreTransactionConsumer {

    private final Map<Integer, Queue<StoreTransaction>> partitionQueues = new ConcurrentHashMap<>();
    private final ExecutorService partitionProcessors;

    public StoreTransactionConsumer() {
        // Create a dedicated thread pool for partition processing
        this.partitionProcessors = Executors.newFixedThreadPool(10);
    }

    @KafkaListener(topics = "store-transactions", groupId = "store-processor")
    public void consumeBatch(
            List<ConsumerRecord<String, StoreTransaction>> records, Acknowledgment ack) {

        // Group records by partition for ordered processing
        Map<Integer, List<ConsumerRecord<String, StoreTransaction>>> recordsByPartition
                = records.stream().collect(Collectors.groupingBy(ConsumerRecord::partition));

        List<CompletableFuture<Void>> processingFutures = new ArrayList<>();

        // Process each partition's records sequentially but partitions in parallel
        for (Map.Entry<Integer, List<ConsumerRecord<String, StoreTransaction>>> entry
                : recordsByPartition.entrySet()) {

            int partition = entry.getKey();
            List<ConsumerRecord<String, StoreTransaction>> partitionRecords = entry.getValue();

            CompletableFuture<Void> future
                    = CompletableFuture.runAsync(
                            () -> {
                                processPartitionRecords(partition, partitionRecords);
                            },
                            partitionProcessors);

            processingFutures.add(future);
        }

        // Wait for all partition processing to complete
        CompletableFuture.allOf(processingFutures.toArray(new CompletableFuture[0]))
                .thenRun(
                        () -> {
                            ack.acknowledge(); // Manual commit after successful processing
                            log.info(
                                    "Processed {} records across {} partitions",
                                    records.size(),
                                    recordsByPartition.size());
                        })
                .exceptionally(
                        ex -> {
                            log.error("Failed to process batch", ex);
                            return null;
                        });
    }

    private void processPartitionRecords(
            int partition, List<ConsumerRecord<String, StoreTransaction>> records) {
        // Sort by offset to maintain order within partition
        records.sort(Comparator.comparing(ConsumerRecord::offset));

        for (ConsumerRecord<String, StoreTransaction> record : records) {
            try {
                processSingleRecord(record);
            } catch (Exception e) {
                log.error(
                        "Failed to process record from partition {} offset {}", partition, record.offset(), e);
                // Continue with next record to avoid blocking entire partition
            }
        }
    }

    private void processSingleRecord(ConsumerRecord<String, StoreTransaction> record) {
        StoreTransaction transaction = record.value();
        String storeId = transaction.getStoreId();

        log.debug(
                "Processing transaction for store: {} from partition: {} offset: {}",
                storeId,
                record.partition(),
                record.offset());

        // Store-specific business logic here
        processStoreTransaction(storeId, transaction);
    }

    private void processStoreTransaction(String storeId, StoreTransaction transaction) {
        // Implement store-specific processing logic
        // This maintains ordering per store since same store always goes to same partition
    }
}
