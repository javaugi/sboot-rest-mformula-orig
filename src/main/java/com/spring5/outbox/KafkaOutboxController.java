/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.outbox;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/kafkaoutbox")
public class KafkaOutboxController {

	private final KafkaProducerService kafkaProducerService;

	private final OutboxService outboxService;

	private final OutboxOrderService outboxOrderService;

	private final OutboxOrderRepository orderRepository;

	private final OutboxRepository outboxRepository;

	@PostMapping("/default")
	public String sendToDefaultTopic(@RequestBody String message) {
		kafkaProducerService.sendToDefaultTopic(message);
		return "Message sent to default topic: " + message;
	}

	@PostMapping("/outbox")
	public String sendToOutboxTopic(@RequestBody OutboxOrder order) {
		outboxOrderService.createOrder(order);
		/*
		 outboxOrderService.createOrder(request); 
         outboxService.createOutboxEvent(
            request.getAggregateType(), request.getAggregateId(), request.getEventType(), request.getPayload() 
          );
      		 */
		return "Outbox event created and sent to outbox topic";
	}

}
