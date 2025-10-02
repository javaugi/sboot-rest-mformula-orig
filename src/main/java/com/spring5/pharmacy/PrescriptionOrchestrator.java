/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.pharmacy;

// PrescriptionOrchestrator.java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PrescriptionOrchestrator {

	@Autowired
	private PharmacyPatientDataService patientDataService;

	@Autowired
	private InsuranceService insuranceService;

	@Autowired
	private AdjudicationService adjudicationService;

	@Transactional
	public PrescriptionResponse processPrescription(PrescriptionRequest request) throws PrescriptionException {
		// Step 1: Validate patient data
		PatientData patient = patientDataService.validate(request.getPatientId());

		// Step 2: Check insurance coverage
		InsuranceResponse insurance = insuranceService.checkCoverage(request.getMedication(), patient.getInsuranceId());

		// Step 3: Adjudicate claim
		AdjudicationResult adjudication = adjudicationService.adjudicate(request.getMedication(),
				insurance.getPlanId());

		// Step 4: If all succeeded, create prescription
		if (adjudication.isApproved()) {
			return new PrescriptionResponse("APPROVED", "Prescription processed");
		}
		else {
			throw new PrescriptionException("Claim denied: " + adjudication.getReason(), HttpStatus.BAD_REQUEST);
		}
	}

}
