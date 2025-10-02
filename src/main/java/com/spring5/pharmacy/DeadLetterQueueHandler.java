/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.pharmacy;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class DeadLetterQueueHandler {

	@KafkaListener(topics = "prescription-errors")
	public void handleFailedEvents(PrescriptionEvent event) {
		// Log error
		// Send to DLQ for manual processing
		// Or initiate compensation
	}

}
