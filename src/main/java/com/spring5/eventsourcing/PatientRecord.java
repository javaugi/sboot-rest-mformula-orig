/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.eventsourcing;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PatientRecord {

    private final UUID patientId;
    private final List<Event> events;

    public PatientRecord(UUID patientId) {
        this.patientId = patientId;
        this.events = new ArrayList<>();
    }

    public void addDiagnosis(String diagnosis) {
        DiagnosisAdded event = new DiagnosisAdded(patientId, diagnosis, Instant.now());
        events.add(event);
    }

    public void addTreatment(String treatment) {
        TreatmentAdded event = new TreatmentAdded(patientId, treatment, Instant.now());
        events.add(event);
    }

    public UUID getPatientId() {
        return patientId;
    }

    public List<Event> getEvents() {
        return events;
    }
}
