/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.jblue;

// @Component
import com.azure.messaging.eventhubs.EventProcessorClient;
import org.springframework.boot.CommandLineRunner;

public class FlightEventProcessorRunner implements CommandLineRunner {

    private final EventProcessorClient processorClient;

    public FlightEventProcessorRunner(EventProcessorClient processorClient) {
        this.processorClient = processorClient;
    }

    @Override
    public void run(String... args) {
        processorClient.start();
        System.out.println("Event Processor Client started...");
    }
}

/*
Java example using Azure Event Hubs with a FlightEvent model that demonstrates:
    Stateful aggregations (e.g., counts or stats per flight)
    Parallel processing across partitions
    Ordering guarantees per key (all events for a flight arrive in order)
    Below is a Spring Boot example combining all three concepts.


FlightEvent - Model
2. Sending Events with Ordering per Key- FlightEventProducer
3. Stateful Aggregation with Parallel Processing - We use FlightEventProcessorClient to process events in parallel per partition while maintaining
    per-flight counts in memory.
4. Starting the Processor - this class

5. What This Gives Us
    Feature                     How It’s Achieved
    Ordering per flightId       Partition key = flightId
    Parallel processing         Multiple partitions → multiple threads
    Stateful aggregation        ConcurrentHashMap counting per flightId
    Checkpointing               BlobCheckpointStore → resume after failure
 */
