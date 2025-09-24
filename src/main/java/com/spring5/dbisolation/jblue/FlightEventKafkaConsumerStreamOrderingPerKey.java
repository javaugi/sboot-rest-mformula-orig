/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.jblue;

//import com.azure.data.cosmos.internal.Bytes;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KGroupedStream;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import org.apache.kafka.common.utils.Bytes;


@Slf4j
@Component
public class FlightEventKafkaConsumerStreamOrderingPerKey {
    private final ObjectMapper mapper = new ObjectMapper();

    @KafkaListener(topics = "flight-events", concurrency = "3")
    public void consumeFlightEvents(String message, @Header(KafkaHeaders.RECEIVED_PARTITION) int partition) {
        System.out.println("Partition: " + partition + " Event: " + message);
    }

    @Bean
    public KStream<String, String> processFlightEvents(StreamsBuilder builder) {

        KStream<String, String> eventsStream = builder.stream("flight-events");

        // Deserialize JSON → FlightEvent
        KStream<String, FlightEvent> flightEventStream = eventsStream
            .mapValues(value -> {
            return processFlightEvent(value);
            /*
                try {
                    return mapper.readValue(value, FlightEvent.class);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
                // */
            });

        // Group by flightId → ordering per key guaranteed
        KGroupedStream<String, FlightEvent> groupedStream = flightEventStream
            .selectKey((key, event) -> event.getId())
            .groupByKey();
        // Stateful aggregation: count per flightId
        KTable<String, Long> counts = groupedStream.count();
        // Output aggregated counts to another topic
        counts.toStream().to("flight-events-counts", Produced.with(org.apache.kafka.common.serialization.Serdes.String(), org.apache.kafka.common.serialization.Serdes.Long()));

        /*
        This guarantees that all events for the same flightId go to the same partition → so Kafka will process them in order per key.
        Now, the question is: How do you actually process each FlightEvent while preserving ordering and also enabling parallel processing across keys?

        1. Use Stateful or Stateless Operations
            You have two main approaches in Kafka Streams after groupByKey():
                Stateless Processing → Use .foreach() or .mapValues() if you don’t need to keep history.
                Stateful Processing → Use .aggregate(), .reduce(), .count() for stateful operations.
            2. Example: Processing Each FlightEvent in Order
         */
        groupedStream
            .aggregate(
                // Initializer: create empty list for each flightId
                () -> new ArrayList<FlightEvent>(),
                // Aggregator: append each event in order
                (flightId, event, aggregate) -> {
                    aggregate.add(event);
                    // Process each event here in order per flightId
                    System.out.println("Processing event for flightId=" + flightId + ": " + event);
                    return aggregate;
                },
                // Store result in a state store
                Materialized.<String, List<FlightEvent>, KeyValueStore<Bytes, byte[]>>as("flight-event-store")
                    .withKeySerde(Serdes.String())
                    .withValueSerde(new FlightEventListSerde())
            )
            .toStream()
            .foreach((flightId, events) -> {
                // Called after each aggregation step → in order per flightId
                System.out.println("Final events for " + flightId + ": " + events);
            });
        /*
        Why this preserves order
            groupByKey(): ensures all events for same key go to same partition.
            aggregate(): processes events in partition order sequentially.
            toStream().foreach(): final ordered output per flightId.

        3. If You Want Just Per-Event Processing (No Aggregation)
            You can skip aggregate() and do:
        groupedStream
            .toStream()
            .foreach((flightId, event) -> {
                System.out.println("Processing ordered event for flightId=" + flightId + ": " + event);
            });

        3. Key Points
            1. Initializer defines initial state per key.
            2. Aggregator defines how to add each event.
            3. Materialized defines the state store details (name, key serde, value serde).
            4. The state store ensures fault tolerance and recovery.
         */

        /*
        4. Parallelism vs Ordering
        Parallel across flightIds → Each partition processes different keys in parallel.
        Ordering within flightId → One thread per partition keeps order.
        If you want more parallelism, increase partition count and consumer instances:
            spring.kafka.listener.concurrency=number_of_partitions
        Each partition is still ordered sequentially.
         */

        return eventsStream;
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
