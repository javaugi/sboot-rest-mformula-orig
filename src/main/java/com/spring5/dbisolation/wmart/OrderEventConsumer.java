/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.wmart;

// import static jakarta.persistence.GenerationType.UUID;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
public class OrderEventConsumer {

    private final IdempotencyService idempotencyService;

    public OrderEventConsumer(IdempotencyService idempotencyService) {
        this.idempotencyService = idempotencyService;
    }

    @KafkaListener(topics = "order-events", groupId = "orders-group")
    public void listen(ConsumerRecord<String, String> record, Acknowledgment ack) {
        String messageId
                = Optional.ofNullable(record.headers().lastHeader("messageId"))
                        .map(h -> new String(h.value(), StandardCharsets.UTF_8))
                        .orElse(UUID.randomUUID().toString());

        if (!idempotencyService.claim("msg:" + messageId, "processing", Duration.ofMinutes(10))) {
            // duplicate — skip
            ack.acknowledge();
            return;
        }
        try {
            // process...
            // save business data
            idempotencyService.saveResponse("msg:" + messageId, "processed");
            ack.acknowledge();
        } catch (Exception ex) {
            // do not ack => message will be redelivered; consider DLQ after retries
            throw ex;
        }
    }
}
