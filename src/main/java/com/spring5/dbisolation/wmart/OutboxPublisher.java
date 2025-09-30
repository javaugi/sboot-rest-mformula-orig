/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.wmart;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OutboxPublisher {

    private final OutboxRepository repo;
    private final KafkaTemplate<String, String> kafka;

    @Scheduled(fixedDelayString = "${outbox.poll-ms:1000}")
    public void publish() {
        List<Outbox> rows = repo.findUnpublished(PageRequest.of(0, 50));
        rows.forEach(
                r -> {
                    try {
                        kafka.send(r.getTopic(), r.getKey(), r.getPayload()).get(5, TimeUnit.SECONDS);
                        r.setPublishedAt(Instant.now());
                        repo.save(r);
                    } catch (Exception ex) {
                        // log and leave for retry
                    }
                });
    }
}
