/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.jblue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * @author javau
 */
public class KafkaProducerService {

	/*
	 * 
	 * 1) Kafka setup & producer Topic considerations Create topics with an appropriate
	 * number of partitions (N) for expected parallelism. Ordering is guaranteed per
	 * partition. Messages with the same key go to the same partition.
	 * 
	 * Example (CLI): kafka-topics --create --topic booking-events --partitions 12
	 * --replication-factor 3 --bootstrap-server broker:9092 kafka-topics --create --topic
	 * flight-events --partitions 12 --replication-factor 3 --bootstrap-server broker:9092
	 * 
	 * KafkaProducer config notes enable.idempotence=true for the producer (ensures
	 * producer-side de-duplication). acks=all for durability. Optionally use Kafka
	 * transactions if you need atomic writes to multiple topics/partitions.
	 * 
	 * Properties sample: spring.kafka.producer.properties.enable.idempotence=true
	 * spring.kafka.producer.properties.acks=all
	 * spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.
	 * StringSerializer
	 * spring.kafka.producer.value-serializer=org.apache.kafka.common.serialization.
	 * StringSerializer
	 */
	private final KafkaTemplate<String, String> kafkaTemplate;

	private final ObjectMapper objectMapper;

	public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
		this.objectMapper = new ObjectMapper();
	}

	public void sendBookingEvent(BookingEvent evt) {
		try {
			String json = objectMapper.writeValueAsString(evt);
			// Key = bookingId => all events for same booking go to same partition =>
			// ordering per booking
			kafkaTemplate.send("booking-events", evt.getBookingId(), json);
		}
		catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	public void sendFlightEvent(FlightEvent evt) {
		try {
			String json = objectMapper.writeValueAsString(evt);
			// Key = flightId/flightNumber => ordering per flight
			kafkaTemplate.send("flight-events", evt.getFlightNumber(), json);
		}
		catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

}
