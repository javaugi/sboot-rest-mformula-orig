/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.jblue;

import com.azure.messaging.eventhubs.EventData;
import com.azure.messaging.eventhubs.EventDataBatch;
import com.azure.messaging.eventhubs.EventHubClientBuilder;
import com.azure.messaging.eventhubs.EventHubProducerClient;
import com.azure.messaging.eventhubs.models.CreateBatchOptions;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;

/**
 * @author javau
 */
public class EventHubProducerService {

    /*
  4) Azure Event Hubs: producer & EventProcessorClient consumer
      Event Hubs uses partition keys similarly: messages with same partition key go to same partition.
      Producer (Event Hubs)
      Dependencies: com.azure:azure-messaging-eventhubs
  EventHubProducerService.java
     */
    private final EventHubProducerClient producerClient;
    private final ObjectMapper mapper = new ObjectMapper();

    public EventHubProducerService(
            @Value("${eventhub.connection}") String conn, @Value("${eventhub.name}") String hubName) {
        this.producerClient
                = new EventHubClientBuilder().connectionString(conn, hubName).buildProducerClient();
    }

    public void sendFlightEvent(FlightEvent evt) {
        try {
            String json = mapper.writeValueAsString(evt);
            CreateBatchOptions options = new CreateBatchOptions().setPartitionKey(evt.getFlightNumber());
            EventDataBatch batch = producerClient.createBatch(options);
            batch.tryAdd(new EventData(json));
            producerClient.send(batch);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendBookingEvent(BookingEvent evt) {
        /* use evt.getBookingId() as partition key */
        try {
            String json = mapper.writeValueAsString(evt);
            CreateBatchOptions options = new CreateBatchOptions().setPartitionKey(evt.getBookingId());
            EventDataBatch batch = producerClient.createBatch(options);
            batch.tryAdd(new EventData(json));
            producerClient.send(batch);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
