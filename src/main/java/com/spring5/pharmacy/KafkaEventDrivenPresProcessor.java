/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.pharmacy;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaEventDrivenPresProcessor {

	public final PrescriptionOrderService pharmacyService;

	public KafkaEventDrivenPresProcessor(PrescriptionOrderService pharmacyService) {
		this.pharmacyService = pharmacyService;
	}

	@KafkaListener(topics = "new-prescriptions", groupId = "pharmacy-group")
	public void processPrescription(ConsumerRecord<String, PrescriptionData> record) {
		if (isDuplicate(record.key())) {
			return; // Idempotency check
		}
		pharmacyService.processOrder(record.value());
	}

	private boolean isDuplicate(String key) {
		return true;
	}

}
