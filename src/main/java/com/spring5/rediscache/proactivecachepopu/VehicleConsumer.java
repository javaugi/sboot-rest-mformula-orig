/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.rediscache.proactivecachepopu;

import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.*;

public class VehicleConsumer {

	private final Consumer<String, GenericRecord> consumer;

	private final String topicName;

	public VehicleConsumer(String bootstrapServers, String schemaRegistryUrl, String topicName, String groupId) {
		this.topicName = topicName;

		Properties props = new Properties();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
				org.apache.kafka.common.serialization.StringDeserializer.class.getName());
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class.getName());
		props.put("schema.registry.url", schemaRegistryUrl);
		// Important for backward compatibility:
		// This allows the deserializer to use the consumer's schema when the producer's
		// schema is
		// newer.
		// It's the default for Avro, but good to be explicit.
		props.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, false);

		// Auto-commit offsets for simplicity, in production consider manual commits
		props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
		props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");

		this.consumer = new KafkaConsumer<>(props);
		this.consumer.subscribe(Collections.singletonList(topicName));
	}

	public void startConsuming() {
		System.out.println("Starting consumer for topic: " + topicName);
		while (true) {
			ConsumerRecords<String, GenericRecord> records = consumer.poll(Duration.ofMillis(100));
			for (ConsumerRecord<String, GenericRecord> record : records) {
				GenericRecord vehicleEvent = record.value();

				// Handle schema versioning dynamically based on the event payload
				// The Avro deserializer will automatically resolve the schema.
				// You can access fields by their name, and if a field doesn't exist in
				// the record's schema
				// but exists in your consumer's reader schema (because of a default value
				// or being
				// optional),
				// it will be handled gracefully (e.g., return null or default value).
				String vin = (String) vehicleEvent.get("vin");
				String make = (String) vehicleEvent.get("make");
				String model = (String) vehicleEvent.get("model");
				int year = (Integer) vehicleEvent.get("year");

				// Check for the 'color' field, which might not exist in V1 events
				Object color = vehicleEvent.get("color"); // Will be null for V1 events

				System.out.printf("Received event: VIN=%s, Make=%s, Model=%s, Year=%d", vin, make, model, year);
				if (color != null) {
					System.out.printf(", Color=%s", color);
				}
				System.out.printf(" (Topic: %s, Partition: %d, Offset: %d)%n", record.topic(), record.partition(),
						record.offset());
			}
		}
	}

	public void close() {
		consumer.close();
	}

	public static void main(String[] args) {
		String bootstrapServers = "localhost:9092"; // Your Kafka broker(s)
		String schemaRegistryUrl = "http://localhost:8081"; // Your Schema Registry URL
		String topic = "vehicle-inventory-events";
		String groupId = "vehicle-inventory-consumer-group";

		VehicleConsumer consumer = new VehicleConsumer(bootstrapServers, schemaRegistryUrl, topic, groupId);
		try {
			consumer.startConsuming();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			consumer.close();
		}
	}

}
