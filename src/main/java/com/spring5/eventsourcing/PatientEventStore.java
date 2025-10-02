/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.eventsourcing;

import java.util.List;
import java.util.UUID;

/**
 * @author javau
 */
public interface PatientEventStore {

	void saveEvents(UUID patientId, List<Event> events);

	List<Event> getEventsForPatient(UUID patientId);

}
