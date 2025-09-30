/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.utils.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ReactiveClinicalReviewService {

    private final ReactiveKafkaProducer kafkaProducer;
    private final ReactiveClinicalRulesEngine rulesEngine;

    public ReactiveClinicalReviewService(
            ReactiveKafkaProducer kafkaProducer, ReactiveClinicalRulesEngine rulesEngine) {
        this.kafkaProducer = kafkaProducer;
        this.rulesEngine = rulesEngine;
    }

    @KafkaListener(topics = "${app.topics.claim-validated}")
    public void processValidatedClaim(ReactiveClaimEvent event) {
        log.info("Reviewing validated claim: {}", event.id);

        rulesEngine
                .applyClinicalRules(event)
                .flatMap(
                        reviewResult -> {
                            ReactiveClaimEvent.ClaimStatus status
                            = reviewResult.isApproved()
                            ? ReactiveClaimEvent.ClaimStatus.APPROVED
                            : ReactiveClaimEvent.ClaimStatus.REJECTED;

                            ReactiveClaimEvent reviewedEvent = new ReactiveClaimEvent();
                            BeanUtils.copyProperties(event, reviewedEvent);
                            reviewedEvent.setStatus(status);

                            return kafkaProducer.sendClaimReviewed(reviewedEvent);
                        })
                .onErrorResume(
                        error -> {
                            log.error("Clinical review failed for claim {}: {}", event.id, error.getMessage());
                            return kafkaProducer.sendToDeadLetterTopic(
                                    event.id, "Clinical review error: " + error.getMessage());
                        })
                .subscribe();
    }

    public void processClaim(ReactiveClaimEvent event) {
    }
}
