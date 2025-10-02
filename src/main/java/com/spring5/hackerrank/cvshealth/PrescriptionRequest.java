/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.hackerrank.cvshealth;

import java.util.List;
import java.util.Objects;

// DTO for incoming prescription request
public class PrescriptionRequest {

	private String orderId;

	private String patientId;

	private String prescriberId;

	private List<MedicationItem> medications;

	private String insuranceProviderId;

	// Add other relevant fields like patient demographics, allergies, etc.
	// Constructor, getters, setters (or use Lombok for brevity)
	public PrescriptionRequest() {
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public String getPrescriberId() {
		return prescriberId;
	}

	public void setPrescriberId(String prescriberId) {
		this.prescriberId = prescriberId;
	}

	public List<MedicationItem> getMedications() {
		return medications;
	}

	public void setMedications(List<MedicationItem> medications) {
		this.medications = medications;
	}

	public String getInsuranceProviderId() {
		return insuranceProviderId;
	}

	public void setInsuranceProviderId(String insuranceProviderId) {
		this.insuranceProviderId = insuranceProviderId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		PrescriptionRequest that = (PrescriptionRequest) o;
		return Objects.equals(orderId, that.orderId) && Objects.equals(patientId, that.patientId)
				&& Objects.equals(prescriberId, that.prescriberId) && Objects.equals(medications, that.medications)
				&& Objects.equals(insuranceProviderId, that.insuranceProviderId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(orderId, patientId, prescriberId, medications, insuranceProviderId);
	}

	@Override
	public String toString() {
		return "PrescriptionRequest{" + "orderId='" + orderId + '\'' + ", patientId='" + patientId + '\''
				+ ", prescriberId='" + prescriberId + '\'' + ", medications=" + medications + ", insuranceProviderId='"
				+ insuranceProviderId + '\'' + '}';
	}

}
