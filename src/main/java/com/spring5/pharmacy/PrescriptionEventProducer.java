/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.pharmacy;

// PrescriptionEventProducer.java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PrescriptionEventProducer {

	@Autowired
	private KafkaTemplate<String, PrescriptionEvent> kafkaTemplate;

	public void sendEvent(String topic, PrescriptionEvent event) {
		kafkaTemplate.send(topic, event);
	}

}
