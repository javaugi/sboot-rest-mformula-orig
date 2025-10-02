/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.wmart;

// @Configuration
import com.fasterxml.jackson.databind.JsonSerializer;
import com.spring5.dbisolation.wmart.trans.StoreIdPartitioner;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.neo4j.kernel.impl.transaction.tracing.TransactionEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

public class KafkaProducerConfig {

	@Value("${spring.kafka.bootstrap-servers}")
	private String bootstrapServers;

	public static final String KAFKA_PRODUCER_TRANS_EVENT = "KAFKA_PRODUCER_TRANS_EVENT";

	public static final String KAFKA_TEMPLATE_TRANS_EVENT = "KAFKA_TEMPLATE_TRANS_EVENT";

	@Bean(name = KAFKA_PRODUCER_TRANS_EVENT)
	public ProducerFactory<String, TransactionEvent> producerFactory() {
		Map<String, Object> configProps = new HashMap<>();
		configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

		// Custom partitioner for store-based partitioning
		configProps.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, StoreIdPartitioner.class);
		// Enable idempotence and compression for better reliability/performance
		configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
		configProps.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");

		// Critical settings for reliability
		configProps.put(ProducerConfig.ACKS_CONFIG, "all"); // Wait for all replicas
		configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true"); // Prevent
																			// producer
																			// duplicates
		configProps.put(ProducerConfig.RETRIES_CONFIG, Integer.MAX_VALUE); // Retry
																			// forever
		configProps.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, "5"); // Required
																					// with
																					// idempotence

		return new DefaultKafkaProducerFactory<>(configProps);
	}

	@Bean(name = KAFKA_TEMPLATE_TRANS_EVENT)
	public KafkaTemplate<String, TransactionEvent> kafkaTemplate() {
		return new KafkaTemplate<>(producerFactory());
	}

}
