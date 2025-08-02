/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.eventsourcing;

import java.time.Instant;
import java.util.UUID;

/**
 *
 * @author javau
 */
public class TreatmentAdded extends Event {
    private final String treatment;

    public TreatmentAdded(UUID patientId, String treatment, Instant timestamp) {
        super(patientId, timestamp);
        this.treatment = treatment;
    }

    public String getTreatment() {
        return treatment;
    }
    
    
}
