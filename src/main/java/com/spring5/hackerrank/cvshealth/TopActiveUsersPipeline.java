/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.hackerrank.cvshealth;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.Windowed;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Properties;
import java.util.PriorityQueue; // Min-Heap
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import org.apache.kafka.streams.kstream.WindowedSerdes;
import org.apache.kafka.streams.state.WindowStore;

/**
 *
 * @author javau
 */
public class TopActiveUsersPipeline {
    
    /*
    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "top-active-users-app");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092"); // Your Kafka brokers
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, ClickEventSerde.class); // Custom ClickEvent Serde

        StreamsBuilder builder = new StreamsBuilder();

        // 1. Consume click events
        builder.stream("click-events", Consumed.with(Serdes.String(), new ClickEventSerde()))
               // Use the userId from the ClickEvent as the new key (if not already keyed)
               .selectKey((key, value) -> value.userId)
               // 2. Apply a 1-hour tumbling window
               .windowedBy(TimeWindows.of(Duration.ofHours(1)).grace(Duration.ofMinutes(5))) // 1-hour window, 5 min grace
               // 3. Group by userId and count clicks per user per window
               .groupByKey(Grouped.with(Serdes.String(), new ClickEventSerde()))
               .count(Materialized.as("user-clicks-hourly")) // Materialize to a KTable
               // 4. Perform Top-N calculation (this part requires custom aggregation or KTable stream processing)
               // For simplicity in this conceptual example, we assume `user-clicks-hourly` is being observed.
               // A more advanced solution would stream this KTable to another KStream,
               // then use a stateful processor or another aggregate with a min-heap.
               .toStream()
               .filter((windowedUserId, count) -> count != null) // Filter out tombstone records
               .groupBy((windowedUserId, count) -> "global-top-n", // Group all updates to a single key for a global top-N
                        Grouped.with(Serdes.String(), Serdes.Long()))
               .aggregate(
                   // Initializer: A map to store counts and a min-heap
                   () -> new HashMap<String, Long>(), // Map to store current counts for all users in the window
                   (key, count, currentAgg) -> {
                       String userId = ((Windowed<String>)key).key();
                       currentAgg.put(userId, count);
                       return currentAgg;
                   },
                   Materialized.<String, Map<String, Long>>as("top-n-agg-store")
                       .withKeySerde(Serdes.String())
                       .withValueSerde(new MyHashMapSerde()) // Custom Serde for Map<String, Long>
               )
               // Now, the KTable `top-n-agg-store` holds the current counts for all users in the active window.
               // To get the actual top 10, you'd need another processor that reads from this KTable.
               // For this interview, describing the min-heap logic is key, actual implementation can be complex.
               .toStream()
               .mapValues((key, userCountsMap) -> {
                   // This is where the min-heap logic would be applied on the 'userCountsMap'
                   PriorityQueue<TopUsers> minHeap = new PriorityQueue<>(10); // Min-heap for top 10

                   userCountsMap.forEach((userId, count) -> {
                       if (minHeap.size() < 10) {
                           minHeap.offer(new TopUsers(userId, count));
                       } else if (count > minHeap.peek().count) {
                           minHeap.poll();
                           minHeap.offer(new TopUsers(userId, count));
                       }
                   });
                   // Convert heap to a sorted list for output (descending order)
                   List<TopUsers> top10 = new ArrayList<>(minHeap);
                   top10.sort(Comparator.comparingLong(t -> t.count).reversed());
                   return top10.toString(); // Outputting as string for simplicity
               })
               .to("top-10-active-users-topic", Produced.with(Serdes.String(), Serdes.String()));

        KafkaStreams streams = new KafkaStreams(builder.build(), props);
        streams.start();

        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }
    // Placeholder Serde for Map<String, Long>
    static class MyHashMapSerde extends Serdes.WrapperSerde<Map<String, Long>> {
        public MyHashMapSerde() { super(null, null); 
    }
    
    // */
}
