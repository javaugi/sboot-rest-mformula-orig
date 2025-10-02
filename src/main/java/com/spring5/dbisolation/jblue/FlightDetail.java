/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.jblue;

import com.azure.spring.data.cosmos.core.mapping.Container;
import com.azure.spring.data.cosmos.core.mapping.PartitionKey;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Container(containerName = "flightDetails")
public class FlightDetail {

	@Id
	private String id;

	@PartitionKey
	private String flightNumber;

	// Flight data
	private String departureAirport;

	private String arrivalAirport;

	private LocalDateTime departureTime;

	// Embedded airline data (pre-joined)
	private AirlineInfo airline;

	// Embedded aircraft data
	private AircraftInfo aircraft;

	// Embedded airport data
	private AirportInfo departureAirportInfo;

	private AirportInfo arrivalAirportInfo;

	@Data
	@Builder(toBuilder = true)
	@NoArgsConstructor
	@AllArgsConstructor
	@Container(containerName = "airlineUpdates")
	public static class AirlineInfo {

		private String airlineCode;

		private String airlineName;

		private String country;

		private String contactPhone;

	}

	@Data
	@Builder(toBuilder = true)
	@NoArgsConstructor
	@AllArgsConstructor
	public static class AircraftInfo {

		private String aircraftType;

		private int capacity;

		private int ageYears;

	}

	@Data
	@Builder(toBuilder = true)
	@NoArgsConstructor
	@AllArgsConstructor
	public static class AirportInfo {

		private String airportCode;

		private String airportName;

		private String city;

		private String country;

	}

}
