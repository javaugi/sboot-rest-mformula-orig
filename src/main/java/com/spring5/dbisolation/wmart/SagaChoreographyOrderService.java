/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.wmart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;

/**
 *
 * @author javau
 */
public class SagaChoreographyOrderService {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    private WmOrderRepository orderRepository;

    public void createOrder(Order order) {
        // 1. Save order in DB as PENDING
        order.setStatus("PENDING");
        orderRepository.save(order);

        // 2. Publish an event for other services
        kafkaTemplate.send("order-events", "OrderCreated:" + order.getOrderId());
    }

    public void cancelOrder(String orderId) {
        // Logic to update order status to CANCELLED
        Order order = orderRepository.findById(Long.valueOf(orderId)).orElseThrow();
        order.setStatus("CANCELLED");
        orderRepository.save(order);
    }
}
