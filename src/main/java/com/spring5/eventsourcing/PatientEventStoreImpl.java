/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.eventsourcing;

import java.util.List;
import java.util.UUID;

public class PatientEventStoreImpl implements PatientEventStore {
    private final EventStore eventStore;

    public PatientEventStoreImpl(EventStore eventStore) {
        this.eventStore = eventStore;
    }

    @Override
    public void saveEvents(UUID patientId, List<Event> events) {
        eventStore.saveEvents(patientId, events);
    }

    @Override
    public List<Event> getEventsForPatient(UUID patientId) {
        return eventStore.getEventsForPatient(patientId);
    }
}
