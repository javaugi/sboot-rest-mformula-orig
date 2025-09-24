/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.jblue;

import com.azure.messaging.eventhubs.EventHubClientBuilder;
import com.azure.messaging.eventhubs.EventProcessorClient;
import com.azure.messaging.eventhubs.EventProcessorClientBuilder;
import com.azure.messaging.eventhubs.checkpointstore.blob.BlobCheckpointStore;
import com.azure.messaging.eventhubs.models.ErrorContext;
import com.azure.messaging.eventhubs.models.EventContext;
import com.azure.storage.blob.BlobContainerAsyncClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

//@Configuration
public class EventHubProcessorConfig {

    /*
    Important
        Set partitionKey on the producer so all events for the same flightId go to the same hub partition.
        EventProcessorClient runs one partition processor per partition concurrently; each partition's events are delivered in order.
     */
    private static final String EH_CONNECTION = "<EVENT_HUB_CONNECTION_STRING>";
    private static final String EH_NAME = "flight-events";
    private static final String STORAGE_CONNECTION = "<BLOB_STORAGE_CONNECTION_STRING>";
    private static final String CONTAINER_NAME = "eventhub-checkpoints";

    private final ObjectMapper mapper = new ObjectMapper();
    // Aggregation store: flightId -> count
    private final Map<String, AtomicInteger> flightEventCount = new ConcurrentHashMap<>();

    @Bean
    public EventProcessorClient eventProcessorClient() {
        BlobContainerAsyncClient blobClient = new BlobContainerClientBuilder()
            .connectionString(STORAGE_CONNECTION)
            .containerName(CONTAINER_NAME)
            .buildAsyncClient();

        return new EventProcessorClientBuilder()
            .consumerGroup(EventHubClientBuilder.DEFAULT_CONSUMER_GROUP_NAME)
            .connectionString(EH_CONNECTION, EH_NAME)
            .checkpointStore(new BlobCheckpointStore(blobClient))
            .processEvent(processEvent())
            .processError(processError())
            .buildEventProcessorClient();
    }

    public EventProcessorClient eventProcessorClient(@Value("${eventhub.connection}") String conn,
        @Value("${eventhub.name}") String hub,
        @Value("${blob.connection}") String blobConn,
        @Value("${blob.container}") String containerName,
        IdempotencyStore idempotencyStore,
        FlightEventProcessor flightProcessor) {

        BlobContainerAsyncClient blobClient = new BlobContainerClientBuilder()
            .connectionString(blobConn)
            .containerName(containerName)
            .buildAsyncClient();

        BlobCheckpointStore checkpointStore = new BlobCheckpointStore(blobClient);

        return new EventProcessorClientBuilder()
            .connectionString(conn, hub)
            .checkpointStore(checkpointStore)
            .consumerGroup(EventHubClientBuilder.DEFAULT_CONSUMER_GROUP_NAME)
            .processEvent(eventContext -> {
                try {
                    String json = eventContext.getEventData().getBodyAsString();
                    // parse JSON -> FlightEvent or BookingEvent
                    // idempotency check, process, then checkpoint
                    FlightEvent evt = mapper.readValue(json, FlightEvent.class);
                    if (!idempotencyStore.isProcessed(evt.getId())) {
                        flightProcessor.handle(evt);
                        idempotencyStore.markProcessed(evt.getId());
                    }
                    eventContext.updateCheckpoint(); // checkpoint after success
                } catch (Exception ex) {

                }
            })
            .processError(errorContext -> {
                // logging / alerting
            })
            .buildEventProcessorClient();
    }

    private Consumer<EventContext> processEvent() {
        return eventContext -> {
            String partitionId = eventContext.getPartitionContext().getPartitionId();
            String eventBody = eventContext.getEventData().getBodyAsString();

            // Simulate parsing FlightEvent (in real use: JSON → FlightEvent object)
            String flightId = eventBody.split(",")[0].split("=")[1]; // crude parsing for demo

            // Stateful aggregation per flightId
            flightEventCount.computeIfAbsent(flightId, k -> new AtomicInteger(0)).incrementAndGet();

            System.out.printf("Partition %s -> %s | Count for flight %s = %d%n",
                partitionId, eventBody, flightId, flightEventCount.get(flightId).get());

            // Save checkpoint after processing
            eventContext.updateCheckpoint();
        };
    }

    private Consumer<ErrorContext> processError() {
        return errorContext -> {
            System.err.printf("Error on partition %s: %s%n",
                errorContext.getPartitionContext().getPartitionId(),
                errorContext.getThrowable().getMessage());
        };
    }

    public Map<String, AtomicInteger> getFlightEventCount() {
        return flightEventCount;
    }
}
