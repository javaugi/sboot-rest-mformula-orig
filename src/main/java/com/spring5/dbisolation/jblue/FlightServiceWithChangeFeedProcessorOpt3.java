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
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@EnableScheduling
public class FlightServiceWithChangeFeedProcessorOpt3 {

	private final CosmosAsyncClient cosmosAsyncClient;

	private final AtomicReference<String> continuationToken = new AtomicReference<>();

	@Scheduled(fixedDelay = 5000) // Poll every 5 seconds
	public void pollChangeFeed() {
		CosmosAsyncDatabase database = cosmosAsyncClient.getDatabase("travelDB");
		CosmosAsyncContainer container = database.getContainer("flightEvents");

		CosmosChangeFeedRequestOptions options;

		if (continuationToken.get() != null) {
			options = CosmosChangeFeedRequestOptions.createForProcessingFromContinuation(continuationToken.get());
		}
		else {
			options = CosmosChangeFeedRequestOptions.createForProcessingFromNow(FeedRange.forFullRange());
		}

		container.queryChangeFeed(options, FlightEvent.class)
			.byPage()
			.take(1) // Process one page at a time
			.subscribe(response -> {
				processEvents(response.getResults());
				continuationToken.set(response.getContinuationToken());
			}, error -> System.err.println("Error: " + error.getMessage()));
	}

	private void processEvents(List<FlightEvent> events) {
		for (FlightEvent event : events) {
			System.out.println("Processed: " + event.getFlightNumber());
			// Your processing logic
		}
	}

}
