/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.jblue;

import com.azure.messaging.eventhubs.*;
import com.azure.messaging.eventhubs.models.CreateBatchOptions;
import com.azure.messaging.eventhubs.models.SendOptions;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;

// s@Service
public class FlightEventProducer {
    // Recommended Architecture
    // [HTTP Client] → [Controller] → [Service Layer] → [FlightEventProducer] → [Azure Event Hubs]

    /*
      Controller: Handles HTTP, validates input.
      Service: Orchestrates business logic, may do retries, transformations.
      Producer: Handles low-level Event Hub calls.

  Why This Is Better
      Async: Client isn’t blocked waiting for Event Hubs publish.
      Service Layer: Allows logging, validation, retries, metrics before sending.
      Extensible: Can replace Event Hubs with Kafka, Service Bus, etc., without touching controllers.
     */

    private final EventHubProducerClient producerClient;

    public FlightEventProducer() {
        this.producerClient
                = new EventHubClientBuilder()
                        .connectionString("<EVENT_HUB_CONNECTION_STRING>", "flight-events")
                        .buildProducerClient();
    }

    public void sendEvent(FlightEvent event) {
        EventDataBatch batch
                = producerClient.createBatch(new CreateBatchOptions().setPartitionKey(event.getId()));
        batch.tryAdd(new EventData(event.toString()));
        producerClient.send(batch);
        System.out.println("Sent event: " + event);
    }

    public void sendEventOption2(FlightEvent event) {
        EventData eventData = new EventData(serializeEvent(event));

        // Partition by flightId → ordering per flightId guaranteed
        SendOptions options = new SendOptions().setPartitionKey(event.getId());

        producerClient.send(Arrays.asList(eventData), options);
    }

    public static byte[] serializeEvent(Object event) {
        try {
            return new ObjectMapper().writeValueAsBytes(event);
        } catch (JsonProcessingException ex) {

        }
        return new byte[0];
    }
}
/*
Separation of Concerns
    The controller should handle HTTP request/response, not event streaming internals.
        Tightly coupling them makes it harder to change your messaging layer later (e.g., switching from Event Hubs to Kafka).

    Backpressure & Reliability
        A direct call means any producer failures block your HTTP request.
        Instead, you might want async processing or a queue in between.

    Scalability
        For large bursts (e.g., during bad weather), directly sending inside a request thread can cause timeouts.
        Better to use Spring’s @Async or a reactive controller with backpressure handling.

    Testing & Maintainability
        With direct calls, unit testing your controller now requires mocking producer internals.

 */
