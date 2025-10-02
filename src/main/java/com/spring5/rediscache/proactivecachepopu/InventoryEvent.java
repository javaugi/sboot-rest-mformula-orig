/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.rediscache.proactivecachepopu;

import java.time.Instant;
import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class InventoryEvent {

	public enum EventType {

		STOCK_UPDATE, RESERVATION, SALE

	}

	String eventId;

	String vin;

	EventType type;

	String dealerId;

	Instant timestamp;

	Map<String, Object> details;

	long version;

}
