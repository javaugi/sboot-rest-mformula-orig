/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.jblue;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

public class FlightEventListSerde implements Serde<List<FlightEvent>> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /*
  3. How It Fits In
      Now Kafka Streams knows how to:
          Serialize List<FlightEvent> into JSON for the state store or topic.
          Deserialize List<FlightEvent> back into objects when reading.
     */
    @Override
    public Serializer<List<FlightEvent>> serializer() {
        return (topic, data) -> {
            try {
                return objectMapper.writeValueAsBytes(data);
            } catch (Exception e) {
                throw new RuntimeException("Serialization failed", e);
            }
        };
    }

    @Override
    public Deserializer<List<FlightEvent>> deserializer() {
        return (topic, data) -> {
            try {
                return objectMapper.readValue(data, new TypeReference<List<FlightEvent>>() {
                });
            } catch (IOException e) {
                throw new RuntimeException("Deserialization failed", e);
            }
        };
    }
}
