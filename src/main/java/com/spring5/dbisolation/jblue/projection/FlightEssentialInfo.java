/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.spring5.dbisolation.jblue.projection;

import java.time.LocalDateTime;

/**
 * @author javau
 */
public interface FlightEssentialInfo {

    String getFlightNumber();

    LocalDateTime getDepartureTime();

    String getDepartureAirport();

    // Virtual property (calculated)
    default String getFlightInfo() {
        return getFlightNumber() + " - " + getDepartureAirport() + " at " + getDepartureTime();
    }
}
