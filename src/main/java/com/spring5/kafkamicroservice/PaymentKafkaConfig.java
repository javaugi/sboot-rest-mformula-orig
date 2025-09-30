/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.kafkamicroservice;

import com.spring5.KafkaBaseConfig;
import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class PaymentKafkaConfig extends KafkaBaseConfig {

    @Bean(name = "paymentReqEventKafkaTemplate")
    public KafkaTemplate<String, PaymentRequestEvent> paymentReqEventKafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean(name = "paymentReqKafkaTemplate")
    public KafkaTemplate<String, AdyenPaymentRequest> paymentReqKafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean(name = "paymentSuccessKafkaTemplate")
    public KafkaTemplate<String, AdyenPaymentSuccessEvent> paymentSuccessEventKafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean(name = "paymentFailedKafkaTemplate")
    public KafkaTemplate<String, AdyenPaymentFailedEvent> paymentFailedEventKafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public KafkaOperations<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public DeadLetterPublishingRecoverer dlqRecoverer() {
        return new DeadLetterPublishingRecoverer(
                kafkaTemplate(), (record, ex) -> new TopicPartition("payment-requests-dlq", -1));
    }

    @Bean
    public DefaultErrorHandler errorHandler() {
        return new DefaultErrorHandler(dlqRecoverer(), new FixedBackOff(1000, 3));
    }
}
