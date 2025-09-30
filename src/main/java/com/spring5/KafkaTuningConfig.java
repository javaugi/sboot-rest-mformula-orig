/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5;

import java.util.Collection;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
@Slf4j
public class KafkaTuningConfig extends KafkaBaseConfig {

    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;

    @Value("${spring.kafka.consumer.concurrency:3}") // Default to 3 consumers
    private int concurrency;

    @Bean
    public ProducerFactory<String, Object> tunedProducerFactory() {
        return new DefaultKafkaProducerFactory<>(tunedAvroProducerConfigs());
    }

    @Bean
    public ConsumerFactory<String, Object> tunedConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(tunedConsumerConfigs());
    }

    // Consumer factory
    @Primary
    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(baseConsumerConfigs());
    }

    // Concurrent listener container factory
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory
                = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(concurrency); // Number of concurrent consumers
        factory
                .getContainerProperties()
                .setAckMode(ContainerProperties.AckMode.MANUAL); // Manual commit
        factory.getContainerProperties().setPollTimeout(3000); // 3 seconds
        return factory;
    }

    @Bean
    public DefaultErrorHandler errorHandler() {
        return new DefaultErrorHandler(new FixedBackOff(0L, 1L)); // Example with FixedBackOff
    }

    @Primary
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactoryRetry(
            DefaultErrorHandler errorHandler) {
        ConcurrentKafkaListenerContainerFactory<String, String> factory
                = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(concurrency);

        // Configure error handler
        factory.setCommonErrorHandler(errorHandler);

        // Configure retry template
        RetryTemplate retryTemplate = new RetryTemplate();
        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(1000);
        backOffPolicy.setMultiplier(2.0);
        backOffPolicy.setMaxInterval(10000);
        retryTemplate.setBackOffPolicy(backOffPolicy);
        retryTemplate.setRetryPolicy(new SimpleRetryPolicy(3));

        factory.setReplyTemplate(kafkaTemplate);

        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String>
            kafkaListenerContainerFactoryRebalance() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory
                = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(concurrency);

        // Custom partition assignment strategy
        factory
                .getContainerProperties()
                .setConsumerRebalanceListener(
                        new ConsumerRebalanceListener() {
                    @Override
                    public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
                        log.info("Partitions revoked: {}", partitions);
                    }

                    @Override
                    public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
                        log.info("Partitions assigned: {}", partitions);
                    }
                });

        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String>
            batchKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory
                = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(concurrency);
        factory.setBatchListener(true); // Enable batch processing

        // Configure batch properties
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.BATCH);
        factory.getContainerProperties().setIdleEventInterval(10000L); // 10 seconds

        return factory;
    }
}

/*
Why Choose Kafka Over Others (e.g., RabbitMQ, ActiveMQ, JMS, Redis Pub/Sub)?
    Criteria            Kafka                                       Other Messaging Systems (e.g., RabbitMQ)
    Throughput          Very high (millions of messages/sec)        Moderate
    Message Durability	Yes, persisted to disk with replication     Often in-memory or transient
    Replayability	Yes, consumers can re-read old messages     Not supported or difficult
    Scalability         Horizontally scalable                       Limited or requires clustering
    Partitioning	Yes, native partitioning for parallelism    Limited
    Stream Processing	Kafka Streams, ksqlDB                       Not available

🚀 Core Advantages of Kafka
High Throughput & Low Latency
    Kafka can handle millions of events per second, even with modest hardware. It’s optimized for batch and stream processing.
Durability and Persistence
    Messages are written to disk and replicated across brokers. This ensures data integrity and fault tolerance.
Scalability
    Kafka supports horizontal scaling via topic partitioning. Producers and consumers can scale independently.
Replayable Event Log
    Kafka keeps messages for a configured time (e.g., 7 days). Consumers can re-read messages from any offset — useful for data reprocessing.
Exactly-Once and At-Least-Once Semantics
    Kafka supports at-least-once delivery by default and can be configured for exactly-once processing in certain workflows.
Decoupling of Services
    Kafka enables loose coupling: producers don’t know who the consumers are, and consumers can join/leave independently.
Integration Ecosystem
    Works well with Apache Spark, Flink, Hadoop, Debezium, and has a powerful Kafka Connect ecosystem for integrating with databases, storage, etc.
Built-in Monitoring
    Kafka exposes detailed metrics via JMX, and integrates easily with Prometheus + Grafana for observability.

🛠️ Example Use Cases
    Real-time fraud detection (e.g., in banking).
    Audit logs for regulatory compliance.
    Website activity tracking (like LinkedIn, Netflix).
    Event sourcing in microservices architecture.
    Stream processing pipelines.

🔁 Kafka vs RabbitMQ (Quick Summary)
Feature             Kafka                       RabbitMQ
Storage Model       Log-based                   Queue-based
Message Retention   Time-based                  Until consumed
Ordering            Partition-order guaranteed	No strict ordering
Use Case            Event streaming             Task queueing
Replay Support      Yes                         No
 */
