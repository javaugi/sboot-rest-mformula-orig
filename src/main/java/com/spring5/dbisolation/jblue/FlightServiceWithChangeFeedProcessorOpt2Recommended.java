/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.jblue;

import com.azure.cosmos.CosmosAsyncClient;
import com.azure.cosmos.CosmosAsyncContainer;
import com.azure.cosmos.CosmosAsyncDatabase;
import com.azure.cosmos.models.CosmosChangeFeedRequestOptions;
import com.azure.cosmos.models.FeedRange;
import com.azure.cosmos.models.FeedResponse;
import com.azure.cosmos.util.CosmosPagedFlux;
import jakarta.annotation.PostConstruct;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class FlightServiceWithChangeFeedProcessorOpt2Recommended {

	private final CosmosAsyncClient cosmosAsyncClient;

	private final CosmosAsyncContainer leaseContainer;

	public FlightServiceWithChangeFeedProcessorOpt2Recommended(CosmosAsyncClient cosmosAsyncClient) {
		this.cosmosAsyncClient = cosmosAsyncClient;

		// Create lease container for change feed processor
		CosmosAsyncDatabase database = cosmosAsyncClient.getDatabase("travelDB");
		this.leaseContainer = database.getContainer("leases");
	}

	@PostConstruct
	public void initializeChangeFeed() {
		startChangeFeedProcessor();
	}

	public void startChangeFeedProcessor() {
		CosmosAsyncDatabase database = cosmosAsyncClient.getDatabase("travelDB");
		CosmosAsyncContainer flightEventsContainer = database.getContainer("flightEvents");

		// Configure change feed options
		CosmosChangeFeedRequestOptions options = CosmosChangeFeedRequestOptions
			.createForProcessingFromBeginning(FeedRange.forFullRange());

		options.setMaxItemCount(100); // Process in batches

		// Create the change feed query
		CosmosPagedFlux<FlightEvent> changeFeedFlux = flightEventsContainer.queryChangeFeed(options, FlightEvent.class);

		// Subscribe to process changes
		changeFeedFlux.byPage()
			.subscribe(feedResponse -> processFeedResponse(feedResponse), error -> handleError(error),
					() -> System.out.println("Change feed processing completed"));
	}

	private void processFeedResponse(FeedResponse<FlightEvent> feedResponse) {
		List<FlightEvent> events = feedResponse.getResults();

		for (FlightEvent event : events) {
			try {
				processFlightEvent(event);
			}
			catch (Exception e) {
				System.err.println("Error processing event: " + e.getMessage());
				// Implement retry logic here
			}
		}

		// Get continuation token for next page
		String continuationToken = feedResponse.getContinuationToken();
		if (continuationToken != null) {
			// Store continuation token for resuming
			storeContinuationToken(continuationToken);
		}
	}

	private void processFlightEvent(FlightEvent event) {
		System.out.println("Processing flight event: " + event.getId());
		System.out.println("Flight: " + event.getFlightNumber());
		System.out.println("Event type: " + event.getEventType());
		System.out.println("Timestamp: " + event.getTimestamp());

		// Your business logic here
		switch (event.getEventType()) {
			case "DEPARTURE":
				handleDeparture(event);
				break;
			case "ARRIVAL":
				handleArrival(event);
				break;
			case "DELAY":
				handleDelay(event);
				break;
			default:
				System.out.println("Unknown event type: " + event.getEventType());
		}
	}

	private void handleDeparture(FlightEvent event) {
		// Update flight status to departed
		System.out.println("Flight " + event.getFlightNumber() + " has departed");
	}

	private void handleArrival(FlightEvent event) {
		// Update flight status to arrived
		System.out.println("Flight " + event.getFlightNumber() + " has arrived");
	}

	private void handleDelay(FlightEvent event) {
		// Handle flight delay
		System.out.println("Flight " + event.getFlightNumber() + " is delayed");
	}

	private void handleError(Throwable error) {
		System.err.println("Change feed error: " + error.getMessage());
		// Implement error handling and retry logic
	}

	private void storeContinuationToken(String token) {
		// Store continuation token in database or cache for resuming
		System.out.println("Storing continuation token: " + token);
	}

	// Method to resume from stored continuation token
	public void resumeFromToken(String continuationToken) {
		CosmosAsyncDatabase database = cosmosAsyncClient.getDatabase("travelDB");
		CosmosAsyncContainer flightEventsContainer = database.getContainer("flightEvents");

		CosmosChangeFeedRequestOptions options = CosmosChangeFeedRequestOptions
			.createForProcessingFromContinuation(continuationToken);

		flightEventsContainer.queryChangeFeed(options, FlightEvent.class).byPage().subscribe(this::processFeedResponse);
	}

}
