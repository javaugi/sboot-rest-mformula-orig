/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.jblue;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class FlightEventService {
    private final FlightEventRepository flightEventRepository;
    private final FlightEventProducer flightEventProducer;

    public Mono<FlightEvent> getFlightEvent(String id, String flightNumber) {
        return Mono.just(FlightEvent.builder().id(id).flightNumber(flightNumber).build());
    }

    @Async  // runs in separate thread → doesn't block request
    public void processEvent(FlightEvent event) {
        // Additional transformations, validations, retries can go here
        event = flightEventRepository.save(event);
        flightEventProducer.sendEvent(event);
    }
}
