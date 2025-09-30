/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.jblue;

import java.util.List;
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
public class FlightServiceWithChangeFeed4PreJoin {

    private final FlightDetailRepository flightDetailRepository;
    private final AirlineRepository airlineRepository;
    private final AirportRepository airportRepository;

    // Use Cosmos DB change feed to keep denormalized data in sync
    // @CosmosDbChangeFeedListener(database = "travelDB", container = "flightEvents")
    public void handleFlightChanges(List<FlightEvent> changes) {
        for (FlightEvent flight : changes) {
            updateFlightDetails(flight);
        }
    }

    private void updateFlightDetails(FlightEvent flight) {
        // Get related data
        Airline airline = airlineRepository.findByAirlineCode(flight.getAirlineCode()).orElse(null);
        AirPort departure
                = airportRepository.findByAirportCode(flight.getDepartureAirport()).orElse(null);
        AirPort arrival = airportRepository.findByAirportCode(flight.getArrivalAirport()).orElse(null);

        // Update denormalized document
        FlightDetail flightDetail
                = flightDetailRepository
                        .findByFlightNumber(flight.getFlightNumber())
                        .orElse(new FlightDetail());

        flightDetail.setFlightNumber(flight.getFlightNumber());
        flightDetail.setDepartureAirport(flight.getDepartureAirport());
        flightDetail.setArrivalAirport(flight.getArrivalAirport());

        if (airline != null) {
            FlightDetail.AirlineInfo airlineInfo = new FlightDetail.AirlineInfo();
            airlineInfo.setAirlineCode(airline.getAirlineCode());
            airlineInfo.setAirlineName(airline.getAirlineName());
            flightDetail.setAirline(airlineInfo);
        }

        flightDetailRepository.save(flightDetail);
    }
}
