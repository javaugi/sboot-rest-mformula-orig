/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.jblue;

import com.azure.messaging.eventhubs.EventData;
import com.azure.messaging.eventhubs.EventHubClientBuilder;
import com.azure.messaging.eventhubs.EventProcessorClient;
import com.azure.messaging.eventhubs.EventProcessorClientBuilder;
import com.azure.messaging.eventhubs.checkpointstore.blob.BlobCheckpointStore;
import com.azure.messaging.eventhubs.models.ErrorContext;
import com.azure.messaging.eventhubs.models.EventContext;
import com.azure.messaging.eventhubs.models.PartitionContext;
import com.azure.storage.blob.BlobContainerAsyncClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * @author javau
 */
public class EventProcessorClientExample {

    public static void main(String[] args) throws InterruptedException {
        String eventHubConnectionString = "Endpoint=sb://...";
        String eventHubName = "my-hub";
        String storageConnectionString = "DefaultEndpointsProtocol=...";
        String storageContainerName = "checkpoint-container";

        // 1. Create a checkpoint store (Azure Blob Storage) to persist progress
        BlobContainerAsyncClient blobAsyncClient
                = new BlobContainerClientBuilder()
                        .connectionString(storageConnectionString)
                        .containerName(storageContainerName)
                        .buildAsyncClient();

        BlobCheckpointStore checkpointStore = new BlobCheckpointStore(blobAsyncClient);

        // 2. Build the Event Processor Client
        EventProcessorClient processorClient
                = new EventProcessorClientBuilder()
                        .connectionString(eventHubConnectionString, eventHubName)
                        .consumerGroup(EventHubClientBuilder.DEFAULT_CONSUMER_GROUP_NAME)
                        .checkpointStore(checkpointStore)
                        .processEvent(PROCESS_EVENT) // <- Your business logic here
                        .processError(PROCESS_ERROR)
                        .buildEventProcessorClient();

        // 3. Start the processor. It will run in the background.
        System.out.println("Starting the event processor...");
        processorClient.start();

        // 4. Let it run... (e.g., in a Spring Boot app, you wouldn't need this sleep)
        Runtime.getRuntime()
                .addShutdownHook(
                        new Thread(
                                () -> {
                                    System.out.println("Stopping processor...");
                                    processorClient.stop();
                                }));

        TimeUnit.MINUTES.sleep(60); // Run for a while
        // processorClient.stop(); // Or stop manually
    }

    // YOUR BUSINESS LOGIC: This is called for every event.
    private static final Consumer<EventContext> PROCESS_EVENT
            = eventContext -> {
                EventData event = eventContext.getEventData();
                PartitionContext partition = eventContext.getPartitionContext();

        System.out.printf(
                "Processing event from Partition %s: Seq %d, Body: %s%n",
                partition.getPartitionId(), event.getSequenceNumber(), event.getBodyAsString());

                // ... process the event (save to DB, call an API, etc.) ...
                // Update checkpoint after successful processing.
                // This stores the offset in Blob Storage so processing can resume from here.
                eventContext.updateCheckpoint();
            };

    private static final Consumer<ErrorContext> PROCESS_ERROR
            = errorContext -> {
                System.err.printf(
                        "Error on Partition %s: %s%n",
                        errorContext.getPartitionContext().getPartitionId(),
                        errorContext.getThrowable().getMessage());
            };
}
