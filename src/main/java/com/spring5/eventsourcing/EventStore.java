/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.eventsourcing;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author javau
 */
public class EventStore {

	private final ConcurrentHashMap<String, List<Event>> map = new ConcurrentHashMap();

	public void saveEvents(UUID patientId, List<Event> events) {
		map.put(patientId.toString(), events);
	}

	public List<Event> getEventsForPatient(UUID patientId) {
		return map.get(patientId.toString());
	}

}
