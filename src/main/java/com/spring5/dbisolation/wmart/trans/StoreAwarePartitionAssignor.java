/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.wmart.trans;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.kafka.clients.consumer.RangeAssignor;
import org.apache.kafka.common.TopicPartition;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StoreAwarePartitionAssignor extends RangeAssignor {

    private static final Logger log = LoggerFactory.getLogger(StoreAwarePartitionAssignor.class);

    @Override
    public Map<String, List<TopicPartition>> assign(Map<String, Integer> partitionsPerTopic,
        Map<String, Subscription> subscriptions) {

        Map<String, List<TopicPartition>> assignment = super.assign(partitionsPerTopic, subscriptions);

        // Log assignment for monitoring
        assignment.forEach((consumer, partitions) -> {
            log.info("Consumer {} assigned partitions: {}", consumer, partitions);

            // Group partitions by storeId pattern (if partition naming follows store pattern)
            Map<String, Long> storeDistribution = partitions.stream()
                .collect(Collectors.groupingBy(
                    tp -> extractStoreIdFromPartition(tp),
                    Collectors.counting()
                ));

            log.info("Store distribution for consumer {}: {}", consumer, storeDistribution);
        });

        return assignment;
    }

    private String extractStoreIdFromPartition(TopicPartition partition) {
        // Custom logic to extract storeId from partition metadata
        // This could be based on your partition naming convention
        return "store-" + (partition.partition() % 10); // Example pattern
    }

    @Override
    public String name() {
        return "store-aware";
    }
}
