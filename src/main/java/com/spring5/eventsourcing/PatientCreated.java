/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.eventsourcing;

import java.time.Instant;
import java.util.UUID;

public class PatientCreated extends Event {
    private final String patientName;

    public PatientCreated(UUID patientId, String patientName, Instant timestamp) {
        super(patientId, timestamp);
        this.patientName = patientName;
    }

    public String getPatientName() {
        return patientName;
    }
}
