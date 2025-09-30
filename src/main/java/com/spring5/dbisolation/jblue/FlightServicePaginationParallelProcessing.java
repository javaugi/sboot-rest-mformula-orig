/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.jblue;

import com.spring5.dbisolation.jblue.projection.FlightProjection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class FlightServicePaginationParallelProcessing {

    private final FlightRepositoryCustomAdvancedPaging customFlightRepository;
    private final FlightEventRepository flightRepository;

    // Example 1: Process large dataset with controlled RU consumption
    public void processAllFlights(String airportCode) {
        String query
                = "SELECT c.flightNumber, c.airlineCode FROM c WHERE c.departureAirport = '"
                + airportCode
                + "'";

        // Process in small batches to avoid RU spikes
        List<FlightEvent> results
                = customFlightRepository.findLargeDatasetWithPagination(query, 100, null);
        results.forEach(this::processFlight);
    }

    // Example 2: Using Spring Data's Pageable
    public void processFlightsWithSpringPagination(String airportCode) {
        int page = 0;
        Pageable pageable = PageRequest.of(page, 50); // 50 items per page

        Page<FlightProjection> resultPage;
        do {
            resultPage
                    = PageableExecutionUtils.getPage(
                            flightRepository.findByDepartureAirportPaginated(airportCode, pageable),
                            pageable,
                            () -> getCount(airportCode));

            processPage(resultPage.getContent());
            pageable = pageable.next();

        } while (resultPage.hasNext());
    }

    // Example 3: Parallel processing for very large datasets
    public void processVeryLargeDataset() {
        String query = "SELECT * FROM c WHERE c.departureTime > '2024-01-01'";
        customFlightRepository.processLargeDatasetInParallel(
                query, 1000, 4); // 4 threads, 1000 items per batch
    }

    private long getCount(String airportCode) {
        return flightRepository.countByDepartureAirport(airportCode);
    }

    private void processPage(List<FlightProjection> flights) {
        flights.forEach(
                flight -> {
                    // Process each flight with minimal data
                    System.out.println("Processing: " + flight.getFlightNumber());
                });
    }

    private void processFlight(FlightEvent flight) {
        // Processing logic
    }
}
