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
import java.util.Objects;

// @Getter
// @Builder
// @With // Provides copy() functionality
@Entity
public class PrescriptionEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private final String eventId;
    private final String correlationId;
    private final String type;

    @OneToOne
    private final PrescriptionData payload;

    private final String status;
    private final String errorMessage;

    // Private constructor used by builder
    private PrescriptionEvent(Builder builder) {
        this.eventId = builder.eventId;
        this.correlationId = builder.correlationId;
        this.type = builder.type;
        this.payload = builder.payload;
        this.status = builder.status;
        this.errorMessage = builder.errorMessage;
    }

    // Copy method to create a new builder with existing values
    public Builder copy() {
        return new Builder()
                .eventId(this.eventId)
                .correlationId(this.correlationId)
                .type(this.type)
                .payload(this.payload)
                .status(this.status)
                .errorMessage(this.errorMessage);
    }

    // Builder class
    public static class Builder {

        private String eventId;
        private String correlationId;
        private String type;
        private PrescriptionData payload;
        private String status;
        private String errorMessage;

        public Builder eventId(String eventId) {
            this.eventId = eventId;
            return this;
        }

        public Builder correlationId(String correlationId) {
            this.correlationId = correlationId;
            return this;
        }

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public Builder payload(PrescriptionData payload) {
            this.payload = payload;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder errorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
            return this;
        }

        public PrescriptionEvent build() {
            // Validate required fields
            Objects.requireNonNull(eventId, "eventId cannot be null");
            Objects.requireNonNull(correlationId, "correlationId cannot be null");
            Objects.requireNonNull(type, "type cannot be null");

            return new PrescriptionEvent(this);
        }
    }

    // Getters
    public long getId() {
        return id;
    }

    public String getEventId() {
        return eventId;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public String getType() {
        return type;
    }

    public PrescriptionData getPayload() {
        return payload;
    }

    public String getStatus() {
        return status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    // Example usage
    public static void main(String[] args) {
        PrescriptionEvent original
                = new PrescriptionEvent.Builder()
                        .eventId("event-123")
                        .correlationId("corr-456")
                        .type("PRESCRIPTION_CREATED")
                        .status("PENDING")
                        .build();

        PrescriptionEvent updated = original.copy().type("PATIENT_VALIDATED").status("SUCCESS").build();

        System.out.println("Original: " + original.getType()); // PRESCRIPTION_CREATED
        System.out.println("Updated: " + updated.getType()); // PATIENT_VALIDATED
    }
}
