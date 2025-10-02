/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.kafkamicroservice;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Id;
import jakarta.persistence.PersistenceContext;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

/*
1. Message Ordering
    Strategies:
        Partitioning/Kafka Topics: Ensure related messages go to the same partition
        Sequence numbers: Include sequence numbers in messages
        Stateful processing: Track the last processed sequence number
2. Idempotency
    Strategies:
        Idempotent operations: Design operations to be safely repeatable
        Deduplication tables: Store processed message IDs
        Idempotent producers: Use Kafka's idempotent producer feature
    Example (Deduplication in a consumer):
3. Retries
    Strategies:
        Exponential backoff: Gradually increase delay between retries
        Dead letter queues: Move failed messages to a DLQ after retries
        Circuit breakers: Stop processing if failures persist
    Example (Exponential backoff with retry):
Best Practices Summary
    Message Ordering:
        Use partitioning keys for related messages
        Implement sequence numbers in your events
        Consider event sourcing pattern for critical ordering needs
    Idempotency:
        Design handlers to be idempotent
        Implement deduplication mechanisms
        Use idempotent producers where available
    Retries:
        Implement exponential backoff
        Set reasonable retry limits
        Use dead letter queues for unprocessable messages
        Monitor and alert on DLQ contents
The specific implementation will vary based on your messaging system (Kafka, RabbitMQ, SQS, etc.) and language/framework, but these patterns are widely applicable.

Best Practices in Java
    For Kafka Consumers:
        Use enable.auto.commit=false and manually commit offsets
        Implement seek-to-beginning logic for reprocessing
    For Spring Kafka:
        Use @Retryable for retries
        Configure DeadLetterPublishingRecoverer for DLQ
        Use ConcurrentKafkaListenerContainerFactory for concurrent processing
    For Deduplication:
        Consider using Redis for faster lookups
        Implement TTL for processed event IDs
    For Ordering:
        Use Kafka's single partition per key guarantee
        Implement sequence numbers in your events

 */
@Service
@Slf4j
public class KafkaMessageOrderingIdempotencyRetries {

	// 1. Message Ordering (Kafka Example)
	public class OrderedKafkaProducer {

		public static void main(String[] args) {
			Properties props = new Properties();
			props.put("bootstrap.servers", "localhost:9092");
			props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
			props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

			// Ensure ordering configuration
			props.put("max.in.flight.requests.per.connection", "1");
			log.info("OrderedKafkaProducer prs {}", props);

			Producer<String, String> producer = new KafkaProducer<>(props);

			// Send ordered messages with the same key
			for (int i = 0; i < 10; i++) {
				ProducerRecord<String, String> record = new ProducerRecord<>("ordered-topic", "key1", "Message " + i);

				producer.send(record, (metadata, exception) -> {
					if (exception != null) {
						exception.printStackTrace();
					}
					else {
						System.out.println("Sent message to: " + metadata.topic() + " partition: "
								+ metadata.partition() + " offset: " + metadata.offset());
					}
				});
			}

			producer.close();
		}

	}

	// 2. Idempotency (Spring Boot with Deduplication)
	@Service
	public class IdempotentConsumer {

		@PersistenceContext
		private EntityManager entityManager;

		@KafkaListener(topics = "orders")
		public void processOrder(OrderEvent event) {
			// Check if event was already processed
			if (eventAlreadyProcessed(event.getId())) {
				return;
			}

			// Process the order (idempotent operation)
			processOrderIdempotently(event);

			// Record processing
			saveProcessedEvent(event.getId());
		}

		private boolean eventAlreadyProcessed(String eventId) {
			return entityManager.createQuery("SELECT 1 FROM ProcessedEvents WHERE eventId = :eventId", Integer.class)
				.setParameter("eventId", eventId)
				.setMaxResults(1)
				.getResultList()
				.size() > 0;
		}

		private void processOrderIdempotently(OrderEvent event) {
			// Your idempotent business logic here
			// Example: "INSERT INTO orders ON CONFLICT DO NOTHING"
		}

		private void saveProcessedEvent(String eventId) {
			ProcessedEvent processedEvent = new ProcessedEvent(eventId);
			entityManager.persist(processedEvent);
		}

	}

	@AllArgsConstructor
	class ProcessedEvent {

		@Id
		private String eventId;

		// constructor, getters, setters

	}

	@Data
	class OrderEvent {

		@Id
		private String id;

		// constructor, getters, setters

	}

	// 3. Retries with Exponential Backoff (Spring Kafka)
	@Service
	public class RetryableConsumer {

		@Retryable(value = { RuntimeException.class }, maxAttempts = 3,
				backoff = @Backoff(delay = 1000, multiplier = 2, maxDelay = 5000))
		@KafkaListener(topics = "payments")
		public void processPayment(String paymentData,
				// @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key,
				@Header(KafkaHeaders.RECEIVED_TOPIC) String topic, @Header(KafkaHeaders.OFFSET) long offset) {

			try {
				// Process payment (might fail)
				processPayment(paymentData);
			}
			catch (Exception e) {
				// After max retries, will go to recover method
				throw new RuntimeException("Payment processing failed", e);
			}
		}

		// DLQ handler for failed messages
		@KafkaListener(topics = "payments.DLT")
		public void dltListener(String paymentData) {
			System.err.println("Received message in DLT: " + paymentData);
			// Handle or log the failed message
		}

		private void processPayment(String paymentData) {
			// Your payment processing logic
		}

	}

	// Combined Approach (Event Sourcing with Deduplication)
	public class EventProcessor {

		// Track last processed sequence number per aggregate

		private final ConcurrentHashMap<String, Long> sequenceTracker = new ConcurrentHashMap<>();

		// Track processed event IDs
		private final ConcurrentHashMap<String, Boolean> processedEvents = new ConcurrentHashMap<>();

		public void processEvent(Event event) {
			// Check for duplicate
			if (processedEvents.containsKey(event.getEventId())) {
				return;
			}

			// Check ordering
			Long lastSequence = sequenceTracker.get(event.getAggregateId());
			if (lastSequence != null && event.getSequenceNumber() <= lastSequence) {
				return; // Out of order event
			}

			try {
				// Process event (idempotent operation)
				handleEvent(event);

				// Update tracking
				sequenceTracker.put(event.getAggregateId(), event.getSequenceNumber());
				processedEvents.put(event.getEventId(), true);
			}
			catch (Exception e) {
				// Implement retry logic here
				handleFailure(event, e);
			}
		}

		private void handleEvent(Event event) {
			// Your business logic
		}

		private void handleFailure(Event event, Exception e) {
			// Implement retry or DLQ logic
		}

	}

	@Data
	class Event {

		private String eventId;

		private String aggregateId;

		private long sequenceNumber;

		// other fields

		// getters and setters

	}

}
