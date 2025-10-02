/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.jblue;

import com.azure.cosmos.ConsistencyLevel;
import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosClientBuilder;
import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.CosmosDatabase;
import com.azure.cosmos.models.PartitionKey;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/bookings")
public class BookingEventController {

	/*
	 * BookingService would validate, add metadata, and call either
	 * KafkaProducerService.sendBookingEvent(event) or
	 * EventHubProducerService.sendBookingEvent(event).
	 */
	private final CosmosContainer container;

	private final BookingEventService bookingService;

	@Bean
	public CosmosClient cosmosClient(@Value("${azure.cosmos.uri}") String uri, @Value("${azure.cosmos.key}") String key,
			@Value("${azure.cosmos.preferredRegion:}") String preferredRegion) {

		CosmosClientBuilder builder = new CosmosClientBuilder().endpoint(uri)
			.key(key)
			.consistencyLevel(ConsistencyLevel.SESSION) // balanced
			.contentResponseOnWriteEnabled(true); // get RU charge on writes

		// Direct mode recommended for performance
		builder.directMode(); // default config; optionally configure connectionPool size,
								// idle timeout

		if (!preferredRegion.isBlank()) {
			builder.preferredRegions(Collections.singletonList(preferredRegion));
		}
		return builder.buildClient();
	}

	@Bean
	public CosmosContainer cosmosContainer(CosmosClient client, @Value("${azure.cosmos.database}") String db,
			@Value("${azure.cosmos.container}") String container) {
		CosmosDatabase database = client.getDatabase(db);
		return database.getContainer(container);
	}

	// BAD: Blocks the thread, killing scalability
	public BookingEvent getFlightEventBad(String id, String partitionKey) {
		return container.readItem(id, new PartitionKey(partitionKey), BookingEvent.class).getItem();
		// .block(); // BLOCKING CALL!
	}

	// GOOD: Returns the reactive type for non-blocking handling
	public Mono<BookingEvent> getFlightEvent(String id, String partitionKey) {
		return Mono.just(container.readItem(id, new PartitionKey(partitionKey), BookingEvent.class).getItem());
		// .map(itemResponse -> itemResponse.getItem());
	}

	// This Mono can be seamlessly integrated into a Spring WebFlux endpoint
	@GetMapping("/{id}")
	public Mono<BookingEvent> getFlight(@PathVariable String id, @RequestParam String bookingId) {
		return bookingService.getBookingEvent(id, bookingId);
	}

	@PostMapping("/events")
	public ResponseEntity<String> publishEvent(@RequestBody BookingEvent event) {
		bookingService.processEvent(event);
		return ResponseEntity.ok("Event accepted for processing");
	}

}
