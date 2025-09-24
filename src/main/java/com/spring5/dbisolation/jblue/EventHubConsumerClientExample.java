/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.jblue;

import com.azure.core.util.IterableStream;
import com.azure.messaging.eventhubs.EventData;
import com.azure.messaging.eventhubs.EventHubClientBuilder;
import com.azure.messaging.eventhubs.EventHubConsumerClient;
import com.azure.messaging.eventhubs.models.EventPosition;
import com.azure.messaging.eventhubs.models.PartitionEvent;
import java.time.Duration;

/**
 *
 * @author javau
 */
public class EventHubConsumerClientExample {
    public static void main(String[] args) throws InterruptedException {
        String connectionString = "Endpoint=sb://...";
        String eventHubName = "my-hub";

        // 1. Create the consumer client for a specific consumer group
        EventHubConsumerClient consumer = new EventHubClientBuilder()
            .connectionString(connectionString, eventHubName)
            .consumerGroup(EventHubClientBuilder.DEFAULT_CONSUMER_GROUP_NAME)
            .buildConsumerClient();

        // 2. Get partition IDs (you need to know which partition to read from)
        System.out.println("Getting partition ids...");
        for (String partitionId : consumer.getPartitionIds()) {
            System.out.println("Partition: " + partitionId);
        }

        // 3. Let's read from partition "0", starting from the earliest available event.
        String targetPartitionId = "0";
        EventPosition startingPosition = EventPosition.earliest();

        System.out.printf("Receiving events from partition %s, starting at %s%n",
            targetPartitionId, startingPosition);

        // 4. Read a batch of 10 events with a 30-second timeout.
        // This is a synchronous call. It blocks until it gets the events or times out.
        IterableStream<PartitionEvent> events = consumer.receiveFromPartition(
            targetPartitionId, 10, startingPosition, Duration.ofSeconds(30));

        int numberOfEvents = 0;
        for (PartitionEvent partitionEvent : events) {
            EventData eventData = partitionEvent.getData();
            System.out.printf("Received event: Seq #: %d, Body: %s%n",
                eventData.getSequenceNumber(),
                eventData.getBodyAsString());
            numberOfEvents++;

            // !!! You are responsible for saving the last sequence number for the next read!
            // long lastSequenceNumber = eventData.getSequenceNumber();
            // storeSequenceNumberForNextRun(lastSequenceNumber + 1); // <-- You must implement this
        }

        if (numberOfEvents == 0) {
            System.out.println("No events received within the timeout.");
        }

        // 5. Close the client. This is NOT a continuous process.
        consumer.close();
    }
}
