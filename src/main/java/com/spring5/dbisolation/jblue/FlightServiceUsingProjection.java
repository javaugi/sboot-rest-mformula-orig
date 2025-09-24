/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.jblue;

import com.spring5.dbisolation.jblue.projection.AirlineFlightCount;
import com.spring5.dbisolation.jblue.projection.FlightEssentialInfo;
import com.spring5.dbisolation.jblue.projection.FlightProjection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class FlightServiceUsingProjection {
    private final FlightEventRepository flightRepository;

    // Example 1: Get flight list for display (only needed fields)
    public List<FlightDisplay> getDepartureBoard(String airportCode) {
        List<FlightProjection> projections = flightRepository.findByDepartureAirportProjection(airportCode);

        return projections.stream()
            .map(proj -> new FlightDisplay(
            proj.getFlightNumber(),
            proj.getAirlineCode(),
            proj.getDepartureTime(),
            proj.getArrivalAirport()
        ))
            .collect(Collectors.toList());
    }

    // Example 2: Get airline statistics
    public Map<String, Integer> getAirlineStatistics(String departureAirport) {
        List<AirlineFlightCount> counts = flightRepository.countFlightsByAirline(departureAirport);

        return counts.stream()
            .collect(Collectors.toMap(
                AirlineFlightCount::getAirlineCode,
                AirlineFlightCount::getFlightCount
            ));
    }

    // Example 3: Using Spring Data's built-in projection
    public List<FlightEssentialInfo> getEssentialFlightInfo(String airlineCode) {
        return flightRepository.findByAirlineCode(airlineCode);
    }
}
