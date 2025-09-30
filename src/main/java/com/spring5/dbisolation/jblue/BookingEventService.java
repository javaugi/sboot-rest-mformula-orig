/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.jblue;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class BookingEventService {

    private final BookingEventRepository bookingEventRepository;
    private final KafkaProducerService kafkaProducerService;
    private final EventHubProducerService eventHubProducerService;

    public Mono<BookingEvent> getBookingEvent(String id, String bookingId) {
        return Mono.just(BookingEvent.builder().id(id).bookingId(bookingId).build());
    }

    @Async // runs in separate thread → doesn't block request
    public void processEvent(BookingEvent event) {
        // Additional transformations, validations, retries can go here
        event = bookingEventRepository.save(event);
        kafkaProducerService.sendBookingEvent(event);
        eventHubProducerService.sendBookingEvent(event);
    }
}
