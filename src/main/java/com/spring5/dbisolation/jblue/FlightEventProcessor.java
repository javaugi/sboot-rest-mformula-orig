/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.jblue;

// import com.spring5.aicloud.data.EventData;
import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.CosmosException;
import com.azure.cosmos.models.CosmosItemRequestOptions;
import com.azure.cosmos.models.PartitionKey;
import com.azure.messaging.eventhubs.EventHubClientBuilder;
import com.azure.messaging.eventhubs.EventProcessorClient;
import com.azure.messaging.eventhubs.EventProcessorClientBuilder;
import com.azure.messaging.eventhubs.checkpointstore.blob.BlobCheckpointStore;
import com.azure.messaging.eventhubs.models.ErrorContext;
import com.azure.messaging.eventhubs.models.EventContext;
import com.azure.storage.blob.BlobContainerAsyncClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FlightEventProcessor {

	private final EventProcessorClient processorClient;

	private final EventProcessorClient processorClient2;

	private final CosmosContainer cosmosContainer; // blocking client

	private final Cache<String, EnrichedReference> refCache; // Redis or Caffeine

	/*
	 * Notes: Replace blocking Cosmos calls with CosmosAsyncContainer & reactive chains
	 * for throughput. Use processBulkOperations for heavy writes during spikes. Wrap
	 * external calls with Resilience4j circuit breakers and retries. Use
	 * CompletableFuture or Reactor for parallel enrichment lookups if necessary.
	 */
	public FlightEventProcessor(CosmosContainer cosmosContainer, @Value("${eventhub.conn}") String conn,
			@Value("${eventhub.hub}") String hub, BlobContainerAsyncClient blobClient,
			Cache<String, EnrichedReference> refCache) {

		this.cosmosContainer = cosmosContainer;
		this.refCache = refCache;

		BlobCheckpointStore checkpointStore = new BlobCheckpointStore(blobClient);
		this.processorClient = new EventProcessorClientBuilder().connectionString(conn, hub)
			.consumerGroup(EventHubClientBuilder.DEFAULT_CONSUMER_GROUP_NAME)
			.processEvent(this::processEvent)
			.processError(this::processError)
			.checkpointStore(checkpointStore)
			.buildEventProcessorClient();

		// or the following
		this.processorClient2 = new EventProcessorClientBuilder()
			.consumerGroup(EventHubClientBuilder.DEFAULT_CONSUMER_GROUP_NAME)
			.connectionString(conn, hub)
			.processEvent(eventContext -> {
				// Events for same partition arrive in order
				String partitionId = eventContext.getPartitionContext().getPartitionId();
				FlightEvent event = deserializeEvent(eventContext.getEventData().getBody());
				processEvent(event);
				System.out.println("Partition: " + partitionId + " Event: " + event);
				eventContext.updateCheckpoint();
			})
			.processError(errorContext -> {
				System.err.println("Error on partition " + errorContext.getPartitionContext().getPartitionId());
			})
			.buildEventProcessorClient();

		this.processorClient.start();
	}

	private void processEvent(FlightEvent event) {
	}

	private FlightEvent deserializeEvent(byte[] value) {
		FlightEvent flightEvent;
		try {
			flightEvent = new ObjectMapper().readValue(value, FlightEvent.class);
			log.info("" + flightEvent);
		}
		catch (IOException ex) {
			log.error("Failed to parse FlightEvent from JSON", ex);
			flightEvent = FlightEvent.builder().build();
		}
		return flightEvent;
	}

	private void processEvent(EventContext ctx) {
		String body = ctx.getEventData().getBodyAsString();
		FlightEvent event = FlightEvent.builder().description(body).build();
		String idempotencyKey = event.getId();

		// Idempotency: try to read by id, skip if exists
		try {
			cosmosContainer.readItem(idempotencyKey, new PartitionKey(event.getFlightNumber()), EnrichedEvent.class);
			// Already processed — checkpoint and return
			ctx.updateCheckpoint();
			return;
		}
		catch (CosmosException e) {
			if (e.getStatusCode() != 404) {
				throw e;
			}
			// 404 => proceed
		}

		// Enrichment (cache first)
		EnrichedReference ref = refCache.getIfPresent(event.getAircraftId());
		if (ref == null) {
			// ref = fetchReferenceFromCosmosOrApi(event.getAircraftId());
			if (ref != null) {
				refCache.put(event.getAircraftId(), ref);
			}
		}

		EnrichedEvent enriched = new EnrichedEvent(event, ref);
		// Persist to Cosmos (upsert with id = eventId)
		cosmosContainer.createItem(enriched, new PartitionKey(enriched.getEvent().getFlightNumber()),
				new CosmosItemRequestOptions());

		// Push to WebPubSub (pseudo)
		// webPubSubClient.sendToSubscribers(enriched.getEvent().getFlightNumber(),
		// enriched);
		// Only checkpoint after success
		ctx.updateCheckpoint();
	}

	private void processError(ErrorContext ctx) {
		log.error("Error partition {}: {}", ctx.getPartitionContext().getPartitionId(), ctx.getThrowable().getMessage(),
				ctx.getThrowable());
		// metrics and alerting logic
	}

	public void handle(FlightEvent evt) {
	}

}
