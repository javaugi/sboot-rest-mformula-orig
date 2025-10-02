/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.hackerrank.cvshealth;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ValidationService {

	// In a real application, these would interact with databases, external APIs, etc.
	public ValidationResult validatePrescription(PrescriptionRequest request) {
		List<String> messages = new ArrayList<>();
		ValidationResult.Status status = ValidationResult.Status.APPROVED;

		// Rule 1: Basic data presence check
		if (request.getMedications() == null || request.getMedications().isEmpty()) {
			messages.add("No medications provided in prescription.");
			status = ValidationResult.Status.REJECTED;
		}

		// Rule 2: Simulate drug-drug interaction check (external system call)
		boolean hasDrugInteraction = simulateDrugInteractionCheck(request);
		if (hasDrugInteraction) {
			messages.add("Potential drug-drug interaction detected.");
			status = ValidationResult.Status.PENDING_REVIEW; // Requires manual review
		}

		// Rule 3: Simulate patient allergy check (external patient history system)
		boolean hasAllergyConflict = simulatePatientAllergyCheck(request);
		if (hasAllergyConflict) {
			messages.add("Medication conflicts with patient allergies.");
			status = ValidationResult.Status.REJECTED;
		}

		// Rule 4: Simulate insurance coverage check (external insurance API)
		boolean isCoveredByInsurance = simulateInsuranceCoverageCheck(request);
		if (!isCoveredByInsurance) {
			messages.add("Medication not covered by primary insurance.");
			if (status == ValidationResult.Status.APPROVED) { // Don't override REJECTED
																// or PENDING_REVIEW
				status = ValidationResult.Status.PENDING_REVIEW; // May need patient
																	// consultation
			}
		}

		// Construct and return the result
		ValidationResult result = new ValidationResult();
		result.setOrderId(request.getOrderId());
		result.setPatientId(request.getPatientId());
		result.setStatus(status);
		result.setValidationMessages(messages);
		return result;
	}

	private boolean simulateDrugInteractionCheck(PrescriptionRequest request) {
		// Mock external API call or database lookup
		return request.getMedications().size() > 1 && request.getMedications().get(0).getDrugCode().equals("DRUG_A")
				&& request.getMedications().get(1).getDrugCode().equals("DRUG_B");
	}

	private boolean simulatePatientAllergyCheck(PrescriptionRequest request) {
		// Mock external patient history API call
		return request.getPatientId().equals("PAT_XYZ")
				&& request.getMedications().stream().anyMatch(m -> m.getDrugCode().equals("DRUG_C"));
	}

	private boolean simulateInsuranceCoverageCheck(PrescriptionRequest request) {
		// Mock external insurance API call
		return !request.getInsuranceProviderId().equals("NO_INSURANCE");
	}

}
