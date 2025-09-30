/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.pharmacy;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.With;

@Getter
@Builder
@With // Provides copy() functionality
@Entity
public class PrescriptionEvent2 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String eventId;
    private String correlationId;
    private String type; // "PRESCRIPTION_CREATED", "PATIENT_VALIDATED", etc.

    @OneToOne
    private PrescriptionData payload;

    private String status; // "SUCCESS", "FAILED"
    private String errorMessage;

    // Example usage with Lombok
    public static void main(String[] args) {
        PrescriptionEvent2 original
                = PrescriptionEvent2.builder()
                        .eventId("event-123")
                        .correlationId("corr-456")
                        .type("PRESCRIPTION_CREATED")
                        .status("PENDING")
                        .build();

        PrescriptionEvent2 updated = original.withType("PATIENT_VALIDATED").withStatus("SUCCESS");

        System.out.println("1 Original: " + original.getType()); // PRESCRIPTION_CREATED
        System.out.println("2 Updated: " + updated.getType()); // PATIENT_VALIDATED
    }
}
