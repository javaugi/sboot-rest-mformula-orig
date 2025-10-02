/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.rediscache;

import com.spring5.RedisConfig;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.connection.stream.StreamReadOptions;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.stereotype.Service;

@Service
public class RedisStreamService {

	private final RedisTemplate<String, String> redisTemplate; // RedisTemplate for
																// general ops

	private final StreamOperations<String, String, String> streamOperations; // Stream-specific
																				// operations

	// Stream and Consumer Group Constants
	private static final String STREAM_KEY = "user:events";

	private static final String CONSUMER_GROUP_NAME = "analytics-group";

	private static final String CONSUMER_NAME = "consumer-app-instance-1";

	public RedisStreamService(@Qualifier(RedisConfig.REDIS_TPL_STR) RedisTemplate<String, String> redisTemplate) {
		this.redisTemplate = redisTemplate;
		this.streamOperations = redisTemplate.opsForStream(); // Get StreamOperations from
																// RedisTemplate
	}

	@PostConstruct
	public void init() {
		// Ensure the consumer group exists when the application starts
		try {
			streamOperations.createGroup(STREAM_KEY, CONSUMER_GROUP_NAME);
			System.out.println("Consumer group '" + CONSUMER_GROUP_NAME + "' created or already exists for stream '"
					+ STREAM_KEY + "'.");
		}
		catch (Exception e) {
			// Group already exists, ignore or log
			if (e.getMessage() != null && !e.getMessage().contains("BUSYGROUP")) {
				System.err.println("Error creating consumer group: " + e.getMessage());
			}
			else {
				System.out.println("Consumer group '" + CONSUMER_GROUP_NAME + "' already exists.");
			}
		}

		// You might want to start a dedicated listener thread here in a real app
		// For simplicity, we'll demonstrate read later
	}

	/**
	 * Adds a new event (record) to the Redis Stream.
	 */
	public RecordId addEvent(String eventType, Map<String, String> payload) {
		Map<String, String> eventData = new HashMap<>(payload);
		eventData.put("eventType", eventType); // Add eventType as a field

		RecordId recordId = streamOperations.add(STREAM_KEY, eventData);
		System.out.println("Added record with ID: " + recordId + " to stream: " + STREAM_KEY);
		return recordId;
	}

	/**
	 * Reads events from the stream using a consumer group.
	 */
	public void readEventsFromGroup() {
		System.out.println("\nReading events for consumer '" + CONSUMER_NAME + "' in group '" + CONSUMER_GROUP_NAME
				+ "' from stream '" + STREAM_KEY + "'...");

		StreamReadOptions options = StreamReadOptions.empty().count(10).block(Duration.ofMillis(100));
		// Read up to 10 records, block for 5 seconds if no new records
		List<MapRecord<String, String, String>> records = streamOperations.read(
				Consumer.from(CONSUMER_GROUP_NAME, CONSUMER_NAME),
				// You can specify multiple stream offsets if reading from multiple
				// streams
				// and configure options like count and block duration
				options, StreamOffset.create(STREAM_KEY, ReadOffset.lastConsumed()) // Read
																					// from
																					// the
																					// last
																					// consumed
																					// offset
		);
		/*
		 * List<MapRecord<String, String, String>> records = streamOperations.read(
		 * Consumer.from(CONSUMER_GROUP_NAME, CONSUMER_NAME),
		 * StreamOffset.create(STREAM_KEY, ReadOffset.lastConsumed()), // Read from the
		 * last consumed offset // You can specify multiple stream offsets if reading from
		 * multiple streams // and configure options like count and block duration
		 * org.springframework.data.redis.connection.stream.StreamReadOptions.empty().
		 * count(10).block(Duration.ofSeconds(5)) ); //
		 */

		if (records == null || records.isEmpty()) {
			System.out.println("No new records for consumer '" + CONSUMER_NAME + "'.");
			return;
		}

		for (MapRecord<String, String, String> record : records) {
			System.out.println("  Record ID: " + record.getId());
			System.out.println("  Stream Key: " + record.getStream());
			System.out.println("  Data: " + record.getValue());

			// Acknowledge the message after processing
			streamOperations.acknowledge(STREAM_KEY, CONSUMER_GROUP_NAME, record.getId());
			System.out.println("  Acknowledged record: " + record.getId());
		}
	}

	/**
	 * Reads all available events from the beginning of the stream (for testing/debugging,
	 * not typical for groups).
	 */
	public void readAllEventsFromStream() {
		System.out.println("\nReading all events from stream '" + STREAM_KEY + "' from the beginning...");
		List<MapRecord<String, String, String>> records = streamOperations.read(StreamOffset.fromStart(STREAM_KEY));

		if (records.isEmpty()) {
			System.out.println("No records in stream '" + STREAM_KEY + "'.");
			return;
		}

		for (MapRecord<String, String, String> record : records) {
			System.out.println("  Record ID: " + record.getId() + ", Data: " + record.getValue());
		}
	}

	/**
	 * Example usage in a main method or another service
	 */
	public static void main(String[] args) throws InterruptedException {
		// This is a placeholder for how you'd get the service in a non-Spring context
		// In a Spring Boot app, you'd autowire this service.
		// For actual runnable demo, you'd need Spring Boot main class and Redis config.
		// Example with a basic RedisTemplate setup (replace with your actual Spring Boot
		// config):
		// JedisConnectionFactory jedisConFactory = new JedisConnectionFactory();
		// jedisConFactory.afterPropertiesSet();
		// RedisTemplate<String, String> template = new RedisTemplate<>();
		// template.setConnectionFactory(jedisConFactory);
		// template.setKeySerializer(new StringRedisSerializer());
		// template.setValueSerializer(new StringRedisSerializer());
		// template.setHashKeySerializer(new StringRedisSerializer());
		// template.setHashValueSerializer(new StringRedisSerializer());
		// template.afterPropertiesSet();
		// RedisStreamService service = new RedisStreamService(template);

		// Simulate usage:
		// service.addEvent("userLogin", Map.of("userId", "user123", "ipAddress",
		// "192.168.1.1"));
		// service.addEvent("productView", Map.of("userId", "user123", "productId",
		// "P001", "category",
		// "electronics"));
		// TimeUnit.SECONDS.sleep(1); // Give time for events to be written
		// service.readEventsFromGroup();
		// service.readEventsFromGroup(); // Read again to see if more arrive or if
		// acknowledged are
		// skipped
		// service.readAllEventsFromStream();
	}

}
