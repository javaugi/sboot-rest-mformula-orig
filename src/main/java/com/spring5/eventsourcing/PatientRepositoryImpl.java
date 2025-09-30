/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.eventsourcing;

import java.util.List;
import java.util.UUID;

public class PatientRepositoryImpl implements PatientRepository {

    private final PatientEventStore eventStore;

    public PatientRepositoryImpl(PatientEventStore eventStore) {
        this.eventStore = eventStore;
    }

    @Override
    public PatientRecord getPatientRecord(UUID patientId) {
        List<Event> events = eventStore.getEventsForPatient(patientId);
        PatientRecord patientRecord = new PatientRecord(patientId);
        // Reconstruct patient record from events
        events.forEach(
                event -> {
                    if (event instanceof DiagnosisAdded) {
                        // Handle DiagnosisAdded event
                    } else if (event instanceof TreatmentAdded) {
                        // Handle TreatmentAdded event
                    }
                    // Handle other events...
                });
        return patientRecord;
    }

    @Override
    public void savePatientRecord(PatientRecord patientRecord) {
        eventStore.saveEvents(patientRecord.getPatientId(), patientRecord.getEvents());
    }
}
