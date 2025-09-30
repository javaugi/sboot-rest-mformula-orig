/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.utils.common;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.TopicConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;

/**
 * @author javau
 */
public class ReactiveKafkaConfig {

    @Value("${app.topics.claim-submitted}")
    private String claimSubmittedTopic;

    @Value("${app.topics.claim-validated}")
    private String claimValidatedTopic;

    @Value("${app.topics.claim-reviewed}")
    private String claimReviewedTopic;

    @Value("${app.topics.claim-processed}")
    private String claimProcessedTopic;

    @Value("${app.topics.dead-letter}")
    private String deadLetterTopic;

    @Bean
    public NewTopic claimSubmittedTopic() {
        return TopicBuilder.name(claimSubmittedTopic)
                .partitions(3)
                .replicas(1)
                .config(TopicConfig.RETENTION_MS_CONFIG, "604800000") // 7 days
                .build();
    }

    @Bean
    public NewTopic deadLetterTopic() {
        return TopicBuilder.name(deadLetterTopic)
                .partitions(1)
                .replicas(1)
                .config(TopicConfig.RETENTION_MS_CONFIG, "2592000000") // 30 days
                .build();
    }
}
