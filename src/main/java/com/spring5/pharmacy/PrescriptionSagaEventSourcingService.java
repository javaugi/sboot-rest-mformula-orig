/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.pharmacy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PrescriptionSagaEventSourcingService {

	@Autowired
	private PrescriptionEventRepository eventRepository;

	@Autowired
	private AdjudicationService adjudicationService;

	@Autowired
	private InsuranceService insuranceService;

	public void handleEvent(PrescriptionEvent event) {
		// Store event
		eventRepository.save(event);

		// Check if all steps completed
		if (isSagaComplete(event.getCorrelationId())) {
			completeSaga(event.getCorrelationId());
		}
		else if (isSagaFailed(event.getCorrelationId())) {
			compensateSaga(event.getCorrelationId());
		}
	}

	private boolean isSagaComplete(String correlationId) {
		return true;
	}

	private void completeSaga(String correlationId) {
	}

	private boolean isSagaFailed(String correlationId) {
		return true;
	}

	private void compensateSaga(String correlationId) {
	}

	// SAGA Compensation:
	public void compensatePrescription(String prescriptionId) {
		// Reverse any completed steps
		adjudicationService.cancelAdjudication(prescriptionId);
		insuranceService.revertCoverageCheck(prescriptionId);
		// Log compensation
	}

}
