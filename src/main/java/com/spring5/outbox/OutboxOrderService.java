/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.outbox;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OutboxOrderService {

	@Autowired
	private OutboxOrderRepository orderRepository;

	@Autowired
	private OutboxRepository outboxRepository;

	@Autowired
	private OutboxEventPublisher publisher;

    /*
    This ensure transaction atomic - the Order and Outbox should be co-locate in the same database schema
    Q: How do you handle database consistency across microservices?
    A: Use Saga or Outbox pattern; avoid distributed transactions; ensure eventual consistency through events.
     */
	@Transactional
	public void createOrder(OutboxOrder order) {
		orderRepository.save(order);
		Outbox event = new Outbox();
		event.setAggregateId(order.getAggregateId());
		event.setEventType("OrderCreated");
		event.setPayload("{ \"orderId\": \"" + order.getId() + "\"}");
		outboxRepository.save(event);
		publisher.publish(event);
	}

}
