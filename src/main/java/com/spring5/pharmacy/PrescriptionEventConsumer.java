/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.pharmacy;

// PrescriptionEventConsumer.java

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class PrescriptionEventConsumer {
    
    @Autowired
    private PharmacyPatientDataService patientDataService;
    
    @Autowired
    private PrescriptionEventProducer eventProducer;
    
    @KafkaListener(topics = "prescription-requests")
    public void handlePrescriptionRequest(PrescriptionEvent event) {
        try {
            // Validate patient
            PatientData patient = patientDataService.validate(event.getPayload().getPatientId());
            
            // Publish next event
            PrescriptionEvent validatedEvent = event.copy()
                    .type("PATIENT_VALIDATED")
                .status("SUCCESS")
                .build();
                
            eventProducer.sendEvent("patient-validated", validatedEvent);
        } catch (Exception ex) {
            PrescriptionEvent failedEvent = event.copy()
                .status("FAILED")
                .errorMessage(ex.getMessage())
                .build();
                
            eventProducer.sendEvent("prescription-errors", failedEvent);
        }
    }
}
