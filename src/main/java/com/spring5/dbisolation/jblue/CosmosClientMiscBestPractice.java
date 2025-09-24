/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.jblue;

import com.azure.cosmos.ChangeFeedProcessor;
import com.azure.cosmos.ChangeFeedProcessorBuilder;
import com.azure.cosmos.ConsistencyLevel;
import com.azure.cosmos.CosmosAsyncClient;
import com.azure.cosmos.CosmosAsyncContainer;
import com.azure.cosmos.CosmosClientBuilder;
import com.azure.cosmos.CosmosDiagnostics;
import com.azure.cosmos.CosmosException;
import com.azure.cosmos.DirectConnectionConfig;
import com.azure.cosmos.models.ChangeFeedProcessorOptions;
import com.azure.cosmos.models.CosmosBulkExecutionOptions;
import com.azure.cosmos.models.CosmosBulkOperations;
import com.azure.cosmos.models.CosmosItemOperation;
import com.azure.cosmos.models.CosmosItemRequestOptions;
import com.azure.cosmos.models.CosmosItemResponse;
import com.azure.cosmos.models.PartitionKey;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.specialized.BlockBlobAsyncClient;
import com.azure.storage.blob.specialized.BlockBlobClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
//import io.github.resilience4j.retry.Retry;
//import io.github.resilience4j.retry.Retry;
import java.time.Duration;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
//import static org.apache.kafka.common.serialization.Serdes.ByteBuffer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
//import reactor.retry.Retry;

@Slf4j
public class CosmosClientMiscBestPractice {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /*
    Anti-patterns to call out
        • Cross-partition queries without good filters.
        • Low-cardinality partition keys (hot partitions).
        • Unbounded item size (>1MB).
        • Overusing Stored Procedures/Triggers for app logic.
     */

    public CosmosAsyncClient asynncClient() {
        CosmosAsyncClient client = new CosmosClientBuilder()
            .credential(new DefaultAzureCredentialBuilder().build())
            .endpoint(System.getenv("COSMOS_ENDPOINT"))
            .consistencyLevel(ConsistencyLevel.SESSION)
            .contentResponseOnWriteEnabled(true)
            .directMode(DirectConnectionConfig.getDefaultConfig()
                .setIdleEndpointTimeout(Duration.ofMinutes(2))
                .setIdleConnectionTimeout(Duration.ofMinutes(2)))
            .gatewayMode() // remove if you prefer Direct; choose one
            .clientTelemetryEnabled(true)
            .key("")
            .multipleWriteRegionsEnabled(true)
            .buildAsyncClient();
        return client;
    }

    public CosmosAsyncContainer asyncContainer(String database, String container) {
        CosmosAsyncContainer asyncContainer = asynncClient().getDatabase(database).getContainer(container);
        return asyncContainer;
    }

    public void ItemWriteWithDiagnosticVisibilityRetries(CosmosAsyncContainer container) {
        FlightEvent event = FlightEvent.builder().flightNumber("12345").build();

        Mono<CosmosItemResponse<FlightEvent>> write
            = container.createItem(event, new PartitionKey(event.getFlightNumber()), new CosmosItemRequestOptions())
                .doOnNext(resp -> {
                    CosmosDiagnostics d = resp.getDiagnostics();
                    System.out.println("RU=" + resp.getRequestCharge() + " | " + d.toString());
                })
                .retryWhen(Retry.backoff(3, Duration.ofMillis(200))
                    .filter(ex -> ex instanceof CosmosException ce && ce.getStatusCode() == 429));

        write.block();
    }

    public void bulkOperations(CosmosAsyncContainer container, List<FlightEvent> items) {
        CosmosBulkExecutionOptions opts = new CosmosBulkExecutionOptions();
        Flux<CosmosItemOperation> ops = Flux.fromIterable(items)
            .map(o -> CosmosBulkOperations.getCreateItemOperation(o, new PartitionKey(o.getFlightNumber())));

        container.executeBulkOperations(ops, opts)
            .collectList()
            .block();
    }

    public void changeFeedProcessor(CosmosAsyncContainer feedContainer, CosmosAsyncContainer leaseContainer, String hostname) {

        ChangeFeedProcessor changeFeedProcessor = new ChangeFeedProcessorBuilder()
            .hostName(hostname)
            .feedContainer(feedContainer)
            .leaseContainer(leaseContainer)
            .handleChanges((List<JsonNode> changes) -> {
            changes.forEach(change -> {
                processFlightEvent(change);
                // Convert JsonNode to FlightEvent
                /*
                    try {
                        FlightEvent flightEvent = objectMapper.readValue(change.toString(), FlightEvent.class);
                        log.info("" + flightEvent);
                    } catch (JsonProcessingException ex) {

                    }
                    // */
                });
            })
            .options(new ChangeFeedProcessorOptions()
                .setStartFromBeginning(true)
                .setLeaseRenewInterval(Duration.ofSeconds(10)))
            .buildChangeFeedProcessor();

        changeFeedProcessor.start().block();
    }

    // Helper method outside the lambda
    private void processFlightEvent(JsonNode change) {
        try {
            FlightEvent flightEvent = objectMapper.treeToValue(change, FlightEvent.class);
            log.info("" + flightEvent);
        } catch (JsonProcessingException ex) {
            log.error("Failed to parse FlightEvent from JSON", ex);
        }
    }

    public void ADLSGen2WithManagedId() {
        BlobServiceClient svc = new BlobServiceClientBuilder()
            .endpoint(System.getenv("ADLS_BLOB_ENDPOINT")) // e.g., https://account.dfs.core.windows.net
            .credential(new DefaultAzureCredentialBuilder().build())
            .buildClient();

        BlobContainerClient cont = svc.getBlobContainerClient("curated");
        BlockBlobClient blob = cont.getBlobClient("tenantA/2025/09/03/file.parquet").getBlockBlobClient();

        BlockBlobAsyncClient asyncClient;
        byte[] data = loadBytes();
        //asyncClient.upload(Flux.just(ByteBuffer.wrap(data)), data.length, true).block();
    }

    private byte[] loadBytes() {
        return null;
    }
}

/*
How to Handle 429 with Exponential Backoff:
Here's how to implement exponential backoff retry logic:

Solution 1: Using Azure SDK Built-in Retry Policies
java
import com.azure.cosmos.CosmosAsyncClient;
import com.azure.cosmos.CosmosClientBuilder;
import com.azure.cosmos.DirectConnectionConfig;
import com.azure.cosmos.CosmosException;

CosmosAsyncClient client = new CosmosClientBuilder()
    .endpoint("your-endpoint")
    .key("your-key")
    .directMode(DirectConnectionConfig.getDefaultConfig()
        .setMaxRetryAttemptsOnThrottledRequests(10) // Max retries on 429
        .setMaxRetryWaitTime(Duration.ofSeconds(30))) // Max wait time
    .buildAsyncClient();
Solution 2: Custom Exponential Backoff Implementation
java
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

.handleChanges((List<JsonNode> changes) -> {
    changes.forEach(change -> {
        retryWithExponentialBackoff(() -> {
            try {
                FlightEvent flightEvent = objectMapper.treeToValue(change, FlightEvent.class);
                log.info("Processed: " + flightEvent);
                // Your processing logic here
            } catch (JsonProcessingException ex) {
                log.error("JSON processing error", ex);
                throw new RuntimeException(ex); // Convert to unchecked for retry logic
            }
        });
    });
})

// Exponential backoff retry utility method
private void retryWithExponentialBackoff(Runnable operation) {
    int maxRetries = 5;
    AtomicInteger attempt = new AtomicInteger(0);

    while (attempt.get() < maxRetries) {
        try {
            operation.run();
            return; // Success - exit method
        } catch (Exception ex) {
            if (isThrottlingException(ex) && attempt.get() < maxRetries - 1) {
                int currentAttempt = attempt.getAndIncrement();
                long waitTime = calculateBackoff(currentAttempt);
                log.warn("429 throttling detected. Attempt {} of {}. Waiting {} ms",
                        currentAttempt + 1, maxRetries, waitTime);

                try {
                    Thread.sleep(waitTime);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Operation interrupted", ie);
                }
            } else {
                throw ex; // Re-throw if not throttling or out of retries
            }
        }
    }
}

private boolean isThrottlingException(Exception ex) {
    return ex instanceof CosmosException &&
           ((CosmosException) ex).getStatusCode() == 429;
}

private long calculateBackoff(int attempt) {
    long baseDelay = 1000; // 1 second base
    long maxDelay = 10000; // 10 seconds max
    long delay = (long) (baseDelay * Math.pow(2, attempt));
    return Math.min(delay, maxDelay);
}
Solution 3: Using Resilience4j Retry Library (Recommended)
java
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;

// Configure retry with exponential backoff
RetryConfig config = RetryConfig.custom()
    .maxAttempts(5)
    .waitDuration(Duration.ofMillis(1000))
    .exponentialBackoff(2.0, Duration.ofMillis(1000), Duration.ofSeconds(10))
    .retryOnException(throwable -> throwable instanceof CosmosException &&
                      ((CosmosException) throwable).getStatusCode() == 429)
    .build();

Retry retry = Retry.of("cosmosRetry", config);

.handleChanges((List<JsonNode> changes) -> {
    changes.forEach(change -> {
        Retry.decorateRunnable(retry, () -> {
            try {
                FlightEvent flightEvent = objectMapper.treeToValue(change, FlightEvent.class);
                log.info("Processed: " + flightEvent);
            } catch (JsonProcessingException ex) {
                log.error("JSON processing error", ex);
                throw new RuntimeException(ex);
            }
        }).run();
    });
});
Key Points about 429 Handling:
Exponential Backoff: Double the wait time after each attempt

Jitter: Add random variation to avoid synchronized retries

Max Retries: Set a reasonable limit (5-10 attempts)

Circuit Breaker: Consider adding circuit breaker pattern for sustained throttling

Monitoring: Log throttling events to adjust your RU/s provisioning

Without proper 429 handling, your application may experience:

Failed operations

Poor performance

Cascading failures

Potential data loss in change feed scenarios

The Azure Cosmos DB Java SDK has built-in retry logic, but for change feed processors, you might need additional handling for downstream processing operations.
*/
