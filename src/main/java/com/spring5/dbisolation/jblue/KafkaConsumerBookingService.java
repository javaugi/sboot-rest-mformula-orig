/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.jblue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;

@RequiredArgsConstructor
public class KafkaConsumerBookingService {
    private final ObjectMapper mapper = new ObjectMapper();
    private final IdempotencyStore idempotencyStore; // see below
    private final BookingProcessor bookingProcessor; // business logic

    @KafkaListener(topics = "booking-events", groupId = "booking-processors", concurrency = "6")
    public void listen(ConsumerRecord<String, String> record, Acknowledgment ack) {
        try {
            BookingEvent evt = mapper.readValue(record.value(), BookingEvent.class);

            // Idempotency check
            if (idempotencyStore.isProcessed(evt.getId())) {
                ack.acknowledge(); // still acknowledge offset to advance
                return;
            }

            // process business logic (synchronous)
            bookingProcessor.handle(evt);

            // mark processed
            idempotencyStore.markProcessed(evt.getId());
            ack.acknowledge(); // commit after success
        } catch (JsonProcessingException e) {
            // log and decide: retry, DLQ, skip
            // Do NOT ack -> will be retried (at-least-once)
            throw new RuntimeException(e);
        }
    }
}
