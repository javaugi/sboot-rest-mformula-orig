/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.utils.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;

@Service
@Slf4j
public class ReactiveKafkaProducer {

    private final KafkaSender<String, Object> kafkaSender;
    private final String claimValidatedTopic;
    private final String deadLetterTopic;

    public ReactiveKafkaProducer(
        KafkaSender<String, Object> kafkaSender,
        @Value("${app.topics.claim-validated}") String claimValidatedTopic,
        @Value("${app.topics.dead-letter}") String deadLetterTopic) {
        this.kafkaSender = kafkaSender;
        this.claimValidatedTopic = claimValidatedTopic;
        this.deadLetterTopic = deadLetterTopic;
    }

    public Mono<Void> sendClaimValidated(ReactiveClaimEvent event) {
        return kafkaSender.send(Mono.just(SenderRecord.create(
            claimValidatedTopic,
            0,
            System.currentTimeMillis(),
            event.id,
            event,
            null
        )))
            .doOnNext(result -> log.info("Sent validated claim: {}", event.id))
            .then();
    }

    public Mono<Boolean> sendToDeadLetterTopic(String claimId, String reason) {
        ReactiveClaimEvent deadLetterEvent = new ReactiveClaimEvent(
            claimId, null, null, null, null, null,
            ReactiveClaimEvent.ClaimStatus.REJECTED, reason
        );

        return kafkaSender.send(Mono.just(SenderRecord.create(
            deadLetterTopic,
            0,
            System.currentTimeMillis(),
            claimId,
            deadLetterEvent,
            null
        )))
            .doOnNext(result -> log.warn("Sent to DLQ: {} - Reason: {}", claimId, reason))
            .then(Mono.just(false));
    }

    public Mono<Boolean> sendClaimReviewed(ReactiveClaimEvent event) {
        return Mono.just(false);
    }
}
