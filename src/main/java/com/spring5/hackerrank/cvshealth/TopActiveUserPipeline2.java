/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.hackerrank.cvshealth;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Properties;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.Windowed;
import org.apache.kafka.streams.kstream.WindowedSerdes;
import org.apache.kafka.streams.state.WindowStore;

/**
 *
 * @author javau
 */
public class TopActiveUserPipeline2 {
    /*
    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "top-active-users-app-v2");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092"); // Your Kafka brokers
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, ClickEventSerde.class);
        props.put(StreamsConfig.STATE_DIR_CONFIG, "/tmp/kafka-streams"); // State directory for RocksDB

        StreamsBuilder builder = new StreamsBuilder();

        // 1. Consume click events
        KStream<String, ClickEvent> clickEventsStream = builder.stream(
            "click-events",
            Consumed.with(Serdes.String(), new ClickEventSerde())
        );

        // 2. Select userId as the key for processing
        KStream<String, ClickEvent> keyedStream = clickEventsStream.selectKey((key, value) -> value.userId);

        // 3. Apply a 1-hour tumbling window and group by userId to count clicks
        // The result of windowedBy followed by groupByKey is a TimeWindowedKStream
        KTable<Windowed<String>, Long> userClicksHourly = keyedStream
            .windowedBy(TimeWindows.of(Duration.ofHours(1)).grace(Duration.ofMinutes(5)))
            .groupByKey(Grouped.with(Serdes.String(), new ClickEventSerde()))
            .count(Materialized.<String, Long, WindowStore<org.apache.kafka.common.utils.Bytes, byte[]>>as("user-clicks-hourly-store") // Specify the inner key/value types for the store
                        .withKeySerde(Serdes.String())
                        .withValueSerde(Serdes.Long())); // This materializes the count per user per window

        // 4. Convert the KTable of windowed counts back to a KStream
        KStream<Windowed<String>, Long> userClicksHourlyStream = userClicksHourly.toStream();

        // 5. Group all updates to a single key for a global top-N calculation within each window
        KTable<Windowed<String>, Map<String, Long>> topNPerWindow = userClicksHourlyStream
            .groupBy(
                (windowedUserId, count) -> windowedUserId, // Group by the entire Windowed<String> key
                Grouped.with(WindowedSerdes.timeWindowedSerdeFrom(String.class), Serdes.Long())
            )
            .aggregate(
                // Initializer: A new HashMap for each window to store user counts
                () -> new HashMap<String, Long>(),
                // Adder: When a new count or update for a userId comes in for this window
                (windowedUserId, count, currentAgg) -> {
                    currentAgg.put(windowedUserId.key(), count); // Store userId -> count
                    return currentAgg;
                },
                // Subtractor: Not needed for simple count, but essential if counts could decrease
                // Combiner for merges if state store is sharded
                Materialized.<Windowed<String>, Map<String, Long>, WindowStore<org.apache.kafka.common.utils.Bytes, byte[]>>as("top-n-agg-store")
                    .withKeySerde(WindowedSerdes.timeWindowedSerdeFrom(String.class))
                    .withValueSerde(new MyHashMapSerde())
            );

        // 6. Process the aggregated map to find the Top 10 users per window
        topNPerWindow.toStream()
            .mapValues((windowedKey, userCountsMap) -> {
                // This is where the min-heap logic is applied
                PriorityQueue<TopUsers> minHeap = new PriorityQueue<>(10); // Min-heap for top 10

                userCountsMap.forEach((userId, count) -> {
                    if (minHeap.size() < 10) {
                        minHeap.offer(new TopUsers(userId, count));
                    } else if (count > minHeap.peek().getCount()) { // Use getCount()
                        minHeap.poll();
                        minHeap.offer(new TopUsers(userId, count));
                    }
                });

                // Convert heap to a sorted list for output (descending order)
                List<TopUsers> top10 = new ArrayList<>(minHeap);
                top10.sort(Comparator.comparingLong(TopUsers::getCount).reversed());
                return top10.toString(); // Outputting as string for simplicity
            })
            .to("top-10-active-users-topic", Produced.with(WindowedSerdes.timeWindowedSerdeFrom(String.class), Serdes.String()));

        KafkaStreams streams = new KafkaStreams(builder.build(), props);
        streams.cleanUp(); // Optional: For development, cleans up local state
        streams.start();

        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }
    // */
}
