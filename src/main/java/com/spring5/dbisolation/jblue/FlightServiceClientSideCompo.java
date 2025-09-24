/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.jblue;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/*
Query tips:
    • Use point reads (container.readItem(id, pk, Class)) when possible — they are cheap (1 RU).
    • Prefer ORDER BY on indexed fields and avoid ORDER BY across partitions if possible.
    • Use projections (select specific fields) rather than SELECT *.
    • Avoid server-side JOINs across many items; use client-side composition or pre-join data.
    • Use maxItemCount smaller when scanning many results to reduce RU spikes and allow parallel processing with continuation tokens.
 */
@RequiredArgsConstructor
@Service
public class FlightServiceClientSideCompo {
    private final FlightEventRepository flightRepository;
    private final AirlineRepository airlineRepository;

    public FlightDetails getFlightWithAirlineDetails(String flightNumber) {
        // 1. Get flight data
        FlightEvent flight = flightRepository.findByFlightNumber(flightNumber)
            .orElseThrow(() -> new RuntimeException("Flight not found"));

        // 2. Get airline data separately
        Airline airline = airlineRepository.findByAirlineCode(flight.getAirlineCode())
            .orElseThrow(() -> new RuntimeException("Airline not found"));

        // 3. Compose results on client side
        return new FlightDetails(flight, airline);
    }

    public List<FlightDetails> getFlightsWithAirlines(String departureAirport) {
        // 1. Get all flights for airport
        List<FlightEvent> flights = flightRepository.findByDepartureAirport(departureAirport);

        // 2. Get unique airline codes
        Set<String> airlineCodes = flights.stream()
            .map(FlightEvent::getAirlineCode)
            .collect(Collectors.toSet());

        // 3. Batch get all airlines
        Map<String, Airline> airlinesMap = airlineRepository.findByAirlineCodeIn(airlineCodes)
            .stream()
            .collect(Collectors.toMap(Airline::getAirlineCode, Function.identity()));

        // 4. Compose results
        return flights.stream()
            .map(flight -> new FlightDetails(flight, airlinesMap.get(flight.getAirlineCode())))
            .collect(Collectors.toList());
    }
}
