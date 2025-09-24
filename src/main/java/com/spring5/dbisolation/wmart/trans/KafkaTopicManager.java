/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.wmart.trans;

import static java.lang.Math.log;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaTopicManager {

    private final AdminClient adminClient;

    public KafkaTopicManager(@Value("${spring.kafka.bootstrap-servers}") String bootstrapServers) {
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        this.adminClient = AdminClient.create(props);
    }

    public void createStorePartitionedTopic(String topicName, int partitions, short replicationFactor) {
        NewTopic newTopic = new NewTopic(topicName, partitions, replicationFactor);

        Map<String, String> configs = new HashMap<>();
        configs.put("cleanup.policy", "compact"); // For key-based compaction if needed
        configs.put("retention.ms", "604800000"); // 7 days retention
        newTopic.configs(configs);

        try {
            adminClient.createTopics(Collections.singleton(newTopic)).all().get(30, TimeUnit.SECONDS);
            log.info("Created topic: {} with {} partitions", topicName, partitions);
        } catch (Exception e) {
            log.warn("Topic creation failed (might already exist): {}", e.getMessage());
        }
    }
}
/*
Key Benefits of This Implementation:
    Per-Store Ordering: Same storeId always routes to same partition
    Locality: Related store transactions processed together
    Scalability: Multiple partitions allow parallel processing
    Order Guarantee: Sequential processing within each partition
    Monitoring: Comprehensive logging and metrics
    Error Handling: Graceful error recovery without blocking entire partitions
    Performance: Batch processing and async operations
*/
