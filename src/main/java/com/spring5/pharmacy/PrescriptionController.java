/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.pharmacy;

// PrescriptionController.java

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/prescriptions")
public class PrescriptionController {
    
    @Autowired
    private PrescriptionOrchestrator orchestrator;
    
    @PostMapping
    public ResponseEntity<PrescriptionResponse> createPrescription(
            @RequestBody PrescriptionRequest request) {
        try {
            PrescriptionResponse response = orchestrator.processPrescription(request);
            return ResponseEntity.ok(response);
        } catch (PrescriptionException ex) {
            return ResponseEntity.status(ex.getStatus())
                .body(new PrescriptionResponse("FAILED", ex.getMessage()));
        }
    }
}   