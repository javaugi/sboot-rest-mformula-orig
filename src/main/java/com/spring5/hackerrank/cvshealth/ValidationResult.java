/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.hackerrank.cvshealth;

import java.util.List;
import java.util.Objects;

public class ValidationResult {
    public enum Status { APPROVED, REJECTED, PENDING_REVIEW }
    private Status status;
    private String orderId;
    private String patientId;
    private List<String> validationMessages; // e.g., "Drug interaction detected", "Insurance not covered"

    // Constructor, getters, setters
    public ValidationResult() {}

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }
    public List<String> getValidationMessages() { return validationMessages; }
    public void setValidationMessages(List<String> validationMessages) { this.validationMessages = validationMessages; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ValidationResult that = (ValidationResult) o;
        return status == that.status && Objects.equals(orderId, that.orderId) && Objects.equals(patientId, that.patientId) && Objects.equals(validationMessages, that.validationMessages);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, orderId, patientId, validationMessages);
    }

    @Override
    public String toString() {
        return "ValidationResult{" +
               "status=" + status +
               ", orderId='" + orderId + '\'' +
               ", patientId='" + patientId + '\'' +
               ", validationMessages=" + validationMessages +
               '}';
    }
}