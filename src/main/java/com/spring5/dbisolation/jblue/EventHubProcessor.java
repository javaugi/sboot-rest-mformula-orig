/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.jblue;

import com.azure.core.credential.TokenCredential;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.messaging.eventhubs.*;
import com.azure.messaging.eventhubs.checkpointstore.blob.BlobCheckpointStore;
import com.azure.messaging.eventhubs.models.ErrorContext;
import com.azure.messaging.eventhubs.models.EventContext;
import com.azure.storage.blob.BlobContainerAsyncClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import java.time.OffsetDateTime;
import java.util.UUID;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EventHubProcessor {

	/*
	 * The Quick Comparison Aspect EventProcessorClient EventHubConsumerClient Primary Use
	 * Case Continuous, high-throughput, fault-tolerant processing Directed, on-demand, or
	 * manual reads from specific partitions Pattern
	 * "I want to process all events from all partitions, forever."
	 * "I want to read some events from a specific partition, right now." Parallelism
	 * Automatic. Manages multiple partitions concurrently. Manual. You manage reading
	 * from each partition. Checkpointing Built-in, automatic. Uses a CheckpointStore
	 * (e.g., Azure Blob). Manual. You must manage your own offset/sequence number
	 * tracking. Fault Tolerance High. Automatically recovers and load balances. None.
	 * It's a simple client; your application logic must handle failures. Complexity
	 * Simpler application logic. The SDK handles the complex orchestration. More complex
	 * application logic. Y
	 */
	private final EventHubConsumerClient consumerClient;

	private final EventHubConsumerAsyncClient consumerAsyncClient;

	private final EventProcessorClient processorClient;

	// Mention of EventProcessorClient which handles load balancing and checkpointing
	// across
	// partitions and multiple consumers. This is crucial.
	private final EventRecordRepository repository;

	private final Counter processedCounter;

	private final Counter duplicateCounter;

	String connectionString = "Endpoint=sb://your-namespace.servicebus.windows.net/;SharedAccessKeyName=RootManageSharedAccessKey;SharedAccessKey=YOUR_SHARED_ACCESS_KEY;EntityPath=your-event-hub-name";

	String consumerGroup = "$DEFAULT"; // Or your custom consumer group name

	/*
	 * Key points in consumer code: • Use partition-aware client (EventProcessorClient)
	 * for balanced consumption. • Checkpoint after successful processing to prevent
	 * reprocessing. • Implement idempotency using eventId dedupe record. • Use DLQ
	 * strategy for poison messages (not shown; forward to another event hub/SQS/SNS or
	 * blob).
	 */
	public EventHubProcessor(@Value("${eventhub.conn}") String conn, @Value("${eventhub.hubName}") String hubName,
			@Value("${azure.storage.checkpoint.container}") String containerName, EventRecordRepository repository,
			MeterRegistry meterRegistry) {

		// Build Blob container client for checkpoint store
		BlobContainerAsyncClient blobContainerAsyncClient = new BlobContainerClientBuilder()
			.connectionString(System.getenv("AZURE_STORAGE_CONN"))
			.containerName(containerName)
			.buildAsyncClient();

		BlobCheckpointStore checkpointStore = new BlobCheckpointStore(blobContainerAsyncClient);

		EventProcessorClientBuilder builder = new EventProcessorClientBuilder()
			.consumerGroup(System.getProperty("eventhub.consumerGroup", "$Default"))
			.connectionString(conn, hubName)
			.processEvent(this::processEvent)
			.processError(this::processError)
			.checkpointStore(checkpointStore);

		EventHubClientBuilder builder2 = new EventHubClientBuilder();
		builder2.connectionString(connectionString);
		TokenCredential credential = new DefaultAzureCredentialBuilder().build();
		builder2.credential("<<fully-qualified-namespace>>", "<<event-hub-name>>", credential);
		builder2.consumerGroup(EventHubClientBuilder.DEFAULT_CONSUMER_GROUP_NAME); // Or a
																					// custom
																					// consumer
																					// group
																					// name

		this.consumerClient = builder2.buildConsumerClient();
		this.consumerAsyncClient = builder2.buildAsyncConsumerClient();

		this.processorClient = builder.buildEventProcessorClient();
		this.repository = repository;
		this.processedCounter = meterRegistry.counter("events.processed");
		this.duplicateCounter = meterRegistry.counter("events.duplicate");
	}

	@PostConstruct
	public void start() {
		this.processorClient.start();
	}

	private void processEvent(EventContext eventContext) {
		try {
			String body = eventContext.getEventData().getBodyAsString();
			String eventId = eventContext.getEventData().getProperties() != null
					? String.valueOf(eventContext.getEventData().getProperties().get("eventId")) : null;

			// Idempotency: skip if already processed
			if (eventId != null && repository.existsByEventId(eventId)) {
				duplicateCounter.increment();
				// checkpoint and skip
				eventContext.updateCheckpoint();
				return;
			}

			EventRecord record = new EventRecord();
			record.setId(UUID.randomUUID().toString());
			record.setEventId(eventId != null ? eventId : record.getId());
			record.setPayload(body);
			record.setProcessedAt(OffsetDateTime.now());
			// record.setProcessedAt(Instant.now());

			// Persist (wrap in try/catch and handle transient failures)
			repository.save(record);
			processedCounter.increment();

			// checkpoint after successful processing
			eventContext.updateCheckpoint();
		}
		catch (Exception ex) {
			// handle exceptions carefully — don't checkpoint on transient failure
			// log & metrics
			// If poison message, forward to DLQ or log for manual inspection
			ex.printStackTrace();
		}
	}

	private void processError(ErrorContext errorContext) {
		// Log partition, exception, consider alerting on repeated errors
		System.err.println("Error in partition: " + errorContext.getPartitionContext().getPartitionId());
		errorContext.getThrowable().printStackTrace();
	}

}
