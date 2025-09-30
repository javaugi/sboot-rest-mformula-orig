/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.misc.wmart;

import com.spring5.dbisolation.wmart.OrderProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;

@SpringBootTest
@EmbeddedKafka(
        partitions = 1,
        topics = {"order-events"})
public class OrderServiceIntegrationTest {

    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    OrderProcessor orderProcessor;

    @BeforeEach
    void setUp() {
    }

    // @Test
    public void testProcessOrder() throws Exception {
        kafkaTemplate.send("order-events", "store1", "{...json...}");
        // wait + assert DB row created
    }
}
