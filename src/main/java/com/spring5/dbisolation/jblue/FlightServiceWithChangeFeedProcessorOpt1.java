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
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class FlightServiceWithChangeFeedProcessorOpt1 {
    private final CosmosAsyncClient cosmosAsyncClient;
    private volatile boolean changeFeedRunning = false;

    @EventListener(ContextRefreshedEvent.class)
    public void startChangeFeedProcessor() {
        if (!changeFeedRunning) {
            changeFeedRunning = true;
            startProcessingChangeFeed();
        }
    }

    private void startProcessingChangeFeed() {
        CosmosAsyncDatabase database = cosmosAsyncClient.getDatabase("travelDB");
        CosmosAsyncContainer container = database.getContainer("flightEvents");

        CosmosChangeFeedRequestOptions options = CosmosChangeFeedRequestOptions
            //.createForProcessingFromBeginning(CosmosChangeFeedStartFrom.BEGINNING);
            .createForProcessingFromBeginning(FeedRange.forFullRange());

        CosmosPagedFlux<FeedResponse> changeFeedFlux = container
            .queryChangeFeed(options, FeedResponse.class);

        changeFeedFlux.subscribe(
            feedResponse -> {
                //for (FlightEvent event : feedResponse.getResults()) {
                //    processFlightEvent(event);
                // }
            },
            error -> {
                System.err.println("Change feed error: " + error.getMessage());
                changeFeedRunning = false;
            },
            () -> {
                System.out.println("Change feed completed");
                changeFeedRunning = false;
            }
        );
    }

    private void processFlightEvent(FlightEvent event) {
        System.out.println("Processing flight event: " + event.getFlightNumber());
        // Your processing logic here
    }
}
