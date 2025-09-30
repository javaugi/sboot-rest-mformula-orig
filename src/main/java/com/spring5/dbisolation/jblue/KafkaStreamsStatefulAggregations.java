/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.jblue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.context.annotation.Bean;

@Slf4j
public class KafkaStreamsStatefulAggregations {

    private static final ObjectMapper mapper = new ObjectMapper();

    private static final FlightEventSerde flightEventSerde = new FlightEventSerde();

    @Bean
    public KStream<String, String> processFlightEvents(StreamsBuilder builder) {
        KStream<String, String> stream
                = builder.stream("flight-events", Consumed.with(Serdes.String(), Serdes.String()));

        KStream<String, FlightEvent> events
                = stream.mapValues(
                        v -> {
                            return processFlightEvent(v);
                            /*
              try {
                  return mapper.readValue(value, FlightEvent.class);
              } catch (JsonProcessingException e) {
                  throw new RuntimeException(e);
              }
              // */
                            // mapper.readValue(v, FlightEvent.class)
                        });

        events
                .selectKey((k, ev) -> ev.getFlightNumber()) // key by flightId
                .groupByKey(Grouped.with(Serdes.String(), flightEventSerde))
                .count(Materialized.as("flight-count-store"))
                .toStream()
                .to("flight-counts", Produced.with(Serdes.String(), Serdes.Long()));

        return stream;
    }

    // Helper method outside the lambda
    private FlightEvent processFlightEvent(String value) {
        FlightEvent flightEvent;
        try {
            flightEvent = mapper.readValue(value, FlightEvent.class);
            log.info("" + flightEvent);
        } catch (JsonProcessingException ex) {
            log.error("Failed to parse FlightEvent from JSON", ex);
            flightEvent = FlightEvent.builder().build();
        }
        return flightEvent;
    }
}
