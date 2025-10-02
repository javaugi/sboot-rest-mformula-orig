/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.jblue;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author javau
 */
public class FlightAnalytics {

	public static void main(String[] args) {
		List<FlightEvent> flights = Arrays
			.asList(/*
					 * new FlightEvent("UA101", "United", "JFK", 360, 299.99, 150), new
					 * FlightEvent("DL202", "Delta", "LAX", 285, 349.99, 180), new
					 * FlightEvent("UA303", "United", "LAX", 290, 319.99, 175), new
					 * FlightEvent("AA404", "American", "JFK", 370, 279.99, 160), new
					 * FlightEvent("DL505", "Delta", "MCO", 120, 199.99, 140) //
					 */);

		// 1. Filtering: Find all flights to JFK
		List<FlightEvent> flightsToJFK = flights.stream()
			.filter(flight -> "JFK".equals(flight.getDestination()))
			.collect(Collectors.toList());
		System.out.println("Flights to JFK: " + flightsToJFK.size());

		// 2. Sorting: Find the top 3 longest flights
		List<FlightEvent> longestFlights = flights.stream()
			.sorted(Comparator.comparingInt(FlightEvent::getDuration).reversed())
			.limit(3)
			.collect(Collectors.toList());
		System.out.println("Longest flights: " + longestFlights);

		// 3. Grouping: Group flights by airline (VERY common question)
		Map<String, List<FlightEvent>> flightsByAirline = flights.stream()
			.collect(Collectors.groupingBy(FlightEvent::getAirlineCode));
		System.out.println("Flights by airline: " + flightsByAirline.keySet());

		// 4. Summarizing: Calculate average flight price per airline
		Map<String, Double> avgPriceByAirline = flights.stream()
			.collect(
					Collectors.groupingBy(FlightEvent::getAirlineCode, Collectors.averagingDouble(FlightEvent::getPrice) // Downstream
																															// collector
					));
		System.out.println("Avg price by airline: " + avgPriceByAirline);

		// 5. Complex Collection: Map<Airline, Map<Destination, List<Flight>>>
		Map<String, Map<String, List<FlightEvent>>> nestedGrouping = flights.stream()
			.collect(Collectors.groupingBy(FlightEvent::getAirlineCode,
					Collectors.groupingBy(FlightEvent::getDestination) // Nested grouping
			));
		System.out.println("Nested grouping: " + nestedGrouping);

		// 6. Mapping & Joining: Create a comma-separated list of flight numbers
		String allFlightNumbers = flights.stream()
			.map(FlightEvent::getFlightNumber) // Transform Flight object -> String
			.collect(Collectors.joining(", "));
		System.out.println("All flight numbers: " + allFlightNumbers);

		// 7. Custom Summary Object: Total passengers and revenue per airline
		record AirlineSummary(String airline, int totalPassengers, double totalRevenue) {

		}

		List<AirlineSummary> airlineSummaries = flights.stream()
			.collect(Collectors.groupingBy(FlightEvent::getAirlineCode,
					Collectors.summarizingDouble(f -> f.getPrice() * f.getPassengers()) // Complex
																						// downstream
			))
			.entrySet()
			.stream()
			.map(entry -> new AirlineSummary(entry.getKey(), (int) entry.getValue().getCount(), // count
																								// of
																								// flights,
																								// not
																								// directly
																								// passengers
					entry.getValue().getSum()))
			.collect(Collectors.toList());
		// Note: A better approach would use a custom collector or a different reducing
		// operation.
		// This is a simplified example.
		System.out.println("Airline summaries: " + airlineSummaries);
	}

}
