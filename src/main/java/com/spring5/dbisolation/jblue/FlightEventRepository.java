/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.jblue;

import com.azure.spring.data.cosmos.repository.CosmosRepository;
import com.spring5.dbisolation.jblue.projection.AirlineFlightCount;
import com.spring5.dbisolation.jblue.projection.FlightEssentialInfo;
import com.spring5.dbisolation.jblue.projection.FlightProjection;
import com.spring5.dbisolation.jblue.projection.FlightSummary;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

// 2. Using native query with query hints
@Repository
public interface FlightEventRepository extends CosmosRepository<FlightEvent, String> {

    Optional<FlightEvent> findByFlightNumber(String flightNumber);

    List<FlightEvent> findByDepartureAirport(String departureAirport);

    // BAD: SELECT * (retrieves entire document)
    @Query("SELECT * FROM c WHERE c.departureAirport = @departureAirport")
    List<FlightEvent> findByDepartureAirportFull(@Param("departureAirport") String departureAirport);

    // GOOD: Projection - select only needed fields
    @Query("SELECT c.flightNumber, c.airlineCode, c.departureTime, c.arrivalAirport "
        + "FROM c WHERE c.departureAirport = @departureAirport")
    List<FlightProjection> findByDepartureAirportProjection(@Param("departureAirport") String departureAirport);

    // Projection with complex fields
    @Query("SELECT c.id, c.flightNumber, c.airline.airlineName, c.departureAirportInfo.city "
        + "FROM c WHERE c.airlineCode = @airlineCode")
    List<FlightSummary> findFlightSummariesByAirline(@Param("airlineCode") String airlineCode);

    // Projection with aggregation
    @Query("SELECT c.airlineCode, COUNT(1) as flightCount "
        + "FROM c WHERE c.departureAirport = @departureAirport "
        + "GROUP BY c.airlineCode")
    List<AirlineFlightCount> countFlightsByAirline(@Param("departureAirport") String departureAirport);

    // Projection with complex fields
    /* Spring has a build-in function for projection
    @Query("SELECT c.flightNumber, c.departureTime, c.departureAirport "
        + "FROM c WHERE c.airlineCode = @airlineCode")
    // */
    List<FlightEssentialInfo> findByAirlineCode(String airlineCode);

    // Method with pagination support
    @Query("SELECT c.flightNumber, c.airlineCode, c.departureTime "
        + "FROM c WHERE c.departureAirport = @departureAirport")
    List<FlightProjection> findByDepartureAirportPaginated(
        @Param("departureAirport") String departureAirport,
        Pageable pageable);

    @Query("SELECT COUNT(1) as count "
        + "FROM c WHERE c.departureAirport = @departureAirport ")
    Long countByDepartureAirport(String departureAirport);
}
