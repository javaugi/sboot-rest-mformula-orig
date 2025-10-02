/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.jblue;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class FlightServiceWithDenormalizedPreJoin {

	private final FlightDetailRepository flightDetailRepository;

	public void createFlightWithPreJoinedData(FlightEvent flight, Airline airline, Aircraft aircraft, AirPort departure,
			AirPort arrival) {

		FlightDetail flightDetail = new FlightDetail();
		flightDetail.setId(flight.getId());
		flightDetail.setFlightNumber(flight.getFlightNumber());
		flightDetail.setDepartureAirport(flight.getDepartureAirport());
		flightDetail.setArrivalAirport(flight.getArrivalAirport());
		flightDetail.setDepartureTime(flight.getDepartureTime());

		// Pre-join airline data
		FlightDetail.AirlineInfo airlineInfo = new FlightDetail.AirlineInfo();
		airlineInfo.setAirlineCode(airline.getAirlineCode());
		airlineInfo.setAirlineName(airline.getAirlineName());
		airlineInfo.setCountry(airline.getCountry());
		airlineInfo.setContactPhone(airline.getContactPhone());
		flightDetail.setAirline(airlineInfo);

		// Pre-join airport data
		FlightDetail.AirportInfo depAirportInfo = new FlightDetail.AirportInfo();
		depAirportInfo.setAirportCode(departure.getAirportCode());
		depAirportInfo.setAirportName(departure.getAirportName());
		depAirportInfo.setCity(departure.getCity());
		depAirportInfo.setCountry(departure.getCountry());
		flightDetail.setDepartureAirportInfo(depAirportInfo);

		FlightDetail.AirportInfo arrAirportInfo = new FlightDetail.AirportInfo();
		arrAirportInfo.setAirportCode(arrival.getAirportCode());
		arrAirportInfo.setAirportName(arrival.getAirportName());
		arrAirportInfo.setCity(arrival.getCity());
		arrAirportInfo.setCountry(arrival.getCountry());
		flightDetail.setArrivalAirportInfo(arrAirportInfo);

		flightDetailRepository.save(flightDetail);
	}

	// Single query gets all related data
	public FlightDetail getCompleteFlightDetails(String flightNumber) {
		return flightDetailRepository.findByFlightNumber(flightNumber).orElse(new FlightDetail());
	}

}
