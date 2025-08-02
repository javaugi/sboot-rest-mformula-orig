/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.eventsourcing;

import java.time.Instant;
import java.util.UUID;

public class DiagnosisAdded extends Event {
    private final String diagnosis;

    public DiagnosisAdded(UUID patientId, String diagnosis, Instant timestamp) {
        super(patientId, timestamp);
        this.diagnosis = diagnosis;
    }

    public String getDiagnosis() {
        return diagnosis;
    }
}
