/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.rediscache.rediskafka;

import java.util.Collections;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.*;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WithPartitionsKafkaTopicAdminClient {
    
    private final ProducerFactory producerFactory;
    
    public void createTopic() {
        try{
            AdminClient admin = AdminClient.create(producerFactory.getConfigurationProperties());
            NewTopic newTopic = new NewTopic("orders", 3, (short) 1); // 3 partitions, replication factor 1

            CreateTopicsResult result = admin.createTopics(Collections.singleton(newTopic));
            result.all().get(); // Wait for topic creation        
        }catch(Exception ex) {
            log.error("Error createTopic ", ex);
        }
    }
    
}
