/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.spring5.dbisolation.jblue.projection;

import java.time.LocalDateTime;

/**
 * @author javau
 */
public interface FlightProjection {

    String getFlightNumber();

    String getAirlineCode();

    LocalDateTime getDepartureTime();

    String getArrivalAirport();
}
