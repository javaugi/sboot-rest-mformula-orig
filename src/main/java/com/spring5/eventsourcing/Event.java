/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.eventsourcing;

import java.time.Instant;
import java.util.UUID;


public abstract class Event {
    private final UUID patientId;
    private final Instant timestamp;

    public Event(UUID patientId, Instant timestamp) {
        this.patientId = patientId;
        this.timestamp = timestamp;
    }

    public UUID getPatientId() {
        return patientId;
    }

    public Instant getTimestamp() {
        return timestamp;
    } 
}
