/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.jblue;

import java.time.LocalDateTime;

/**
 * @author javau
 */
public class FlightDetails {

    private String flightNumber;
    private String airlineCode;
    private String airlineName;
    private String departureAirport;
    private String arrivalAirport;
    private LocalDateTime departureTime;
    private String country;

    public FlightDetails(FlightEvent flight, Airline airline) {
        this.flightNumber = flight.getFlightNumber();
        this.airlineCode = flight.getAirlineCode();
        this.airlineName = airline.getAirlineName();
        this.departureAirport = flight.getDepartureAirport();
        this.arrivalAirport = flight.getArrivalAirport();
        this.departureTime = flight.getDepartureTime();
        this.country = airline.getCountry();
    }
}
