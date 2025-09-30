/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5;

import com.fasterxml.jackson.databind.ser.std.StringSerializer;
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.stereotype.Service;

@Configuration
@Service
public class KafkaBaseConfig {

    public static final String KAFKA_DEF_TOPIC = "my-default-topic";

    public static final String KAFKA_TMPL_AUDIT_EVENT = "auditEventKafkaTemplate";
    public static final String KAFKA_TMPL_TRADE_EVENT = "tradeEventKafkaTemplate";
    public static final String KAFKA_TMPL_DOC_EVENT = "docEventKafkaTemplate";
    public static final String KAFKA_TMPL_OBJ = "objectKafkaTemplate";
    public static final String KAFKA_TMPL_STR = "stringKafkaTemplate";

    @Value("${spring.kafka.bootstrap-servers}")
    protected String bootstrapServers; // "127.0.0.1:9092"

    @Value("${spring.kafka.schema-registry-url}")
    protected String schemaRegistryUrl; // 127.0.0.1:8081

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId; // my-kafka-consumer-group

    public Map<String, Object> baseProducerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        // props.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl);
        props.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl);

        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return props;
    }

    public Map<String, Object> tunedProducerConfigs() {
        Map<String, Object> props = baseProducerConfigs();
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384 * 4); // 64KB
        props.put(ProducerConfig.LINGER_MS_CONFIG, 10);
        props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "zstd");
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432 * 2); // 64MB

        return props;
    }

    public Map<String, Object> baseConsumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false); // Manual commit
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 100); // Max records per poll
        return props;
    }

    public Map<String, Object> tunedConsumerConfigs() {
        Map<String, Object> props = baseConsumerConfigs();
        props.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, 16384); // 16KB
        props.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, 500);
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 500);

        return props;
    }

    public Map<String, Object> baseAvroProducerConfigs() {
        Map<String, Object> props = baseProducerConfigs();
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        return props;
    }

    public Map<String, Object> tunedAvroProducerConfigs() {
        Map<String, Object> props = tunedProducerConfigs();
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        return props;
    }

    @Primary
    @Bean
    public ProducerFactory producerFactory() {
        return new DefaultKafkaProducerFactory<>(baseAvroProducerConfigs());
    }
}
/*
basically means: Kafka isn’t running, or your app can’t reach it on localhost:9092.

Let’s fix that step-by-step 👇

✅ What This Means
The Kafka AdminClient (used for broker metadata or topic operations) is trying to connect to localhost:9092 and fails because:

❌ Kafka isn't running

❌ It's running on a different port or host

❌ Docker network setup isn’t bridged correctly (if using containers)

✅ Step-by-Step Fix
🔍 1. Is Kafka running?
If you're using Kafka locally, start both Zookeeper and Kafka:

If installed via tarball:
# Start Zookeeper (if not using KRaft)
bin/zookeeper-server-start.sh config/zookeeper.properties

# In another terminal, start Kafka broker
bin/kafka-server-start.sh config/server.properties
If using Docker (recommended):
docker-compose up
Your docker-compose.yml should expose port 9092 and set up advertised listeners:

services:
  kafka:
    image: confluentinc/cp-kafka:7.4.0
    ports:
      - "9092:9092"
    environment:
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
🔍 2. Check bootstrap-servers config
In your Spring Boot app (application.yml or application.properties):

spring:
  kafka:
    bootstrap-servers: localhost:9092
Or in application.properties:

properties
spring.kafka.bootstrap-servers=localhost:9092
✅ Make sure it matches the actual port Kafka is running on.

🔍 3. Kafka is in Docker, App is Local?
Then localhost:9092 might not work. Try:

spring:
  kafka:
    bootstrap-servers: 127.0.0.1:9092
Or update KAFKA_ADVERTISED_LISTENERS in Docker to be:

KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://host.docker.internal:9092
🔍 4. Kafka Logs
Check Kafka logs to ensure it started correctly and is listening on the expected port. Look for:

[KafkaServer id=0] started (kafka.server.KafkaServer)
🧪 Optional: Test with Kafka CLI
Run:

bin/kafka-topics.sh --bootstrap-server localhost:9092 --list
If this fails, it confirms Kafka isn’t reachable.
 */
