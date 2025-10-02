/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.jblue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

public class FlightEventSerde implements Serde<FlightEvent> {

	private final ObjectMapper objectMapper = new ObjectMapper();

	/*
	 * 3. How It Fits In Now Kafka Streams knows how to: Serialize List<FlightEvent> into
	 * JSON for the state store or topic. Deserialize List<FlightEvent> back into objects
	 * when reading.
	 */
	@Override
	public Serializer<FlightEvent> serializer() {
		return (topic, data) -> {
			try {
				return objectMapper.writeValueAsBytes(data);
			}
			catch (JsonProcessingException e) {
				throw new RuntimeException("Serialization failed", e);
			}
		};
	}

	@Override
	public Deserializer<FlightEvent> deserializer() {
		return (topic, data) -> {
			try {
				return objectMapper.readValue(data, new TypeReference<FlightEvent>() {
				});
			}
			catch (IOException e) {
				throw new RuntimeException("Deserialization failed", e);
			}
		};
	}

}
