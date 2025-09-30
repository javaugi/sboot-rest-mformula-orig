/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.jblue;

import com.azure.cosmos.ConsistencyLevel;
import com.azure.cosmos.CosmosAsyncClient;
import com.azure.cosmos.CosmosAsyncContainer;
import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosClientBuilder;
import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.CosmosException;
import com.azure.cosmos.models.CosmosBulkItemResponse;
import com.azure.cosmos.models.CosmosBulkOperations;
import com.azure.cosmos.models.CosmosItemOperation;
import com.azure.cosmos.models.CosmosItemRequestOptions;
import com.azure.cosmos.models.CosmosItemResponse;
import com.azure.cosmos.models.PartitionKey;
import java.lang.reflect.InvocationTargetException;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Slf4j
@Service
@RequiredArgsConstructor
public class CosmosDBBatchWriter {

    public static final String CONTAINER_BEAN_FOR_REUSE = "";

    private final CosmosClient cosmosClient;
    private final CosmosAsyncClient cosmosAsyncClient;
    private final String databaseName;
    private final String containerName;

    /*
  Question: Implement a method in Java that uses the Azure Cosmos DB SQL API to perform a batch insert of a list of items. Your code should
      demonstrate best practices, including error handling, connection management, and throughput optimization.
     */

 /*
  @Bean(name = CONTAINER_BEAN_FOR_REUSE)
  public CosmosContainer cosmosContainer(CosmosClient client, CosmosAsyncClient cosmosAsyncClient,
      @Value("${azure.cosmos.database}") String db,
      @Value("${azure.cosmos.container}") String container) {
      CosmosDatabase database = client.getDatabase(db);
      return database.getContainer(container);
  }
  // */
    public CosmosDBBatchWriter(String host, String key, String databaseName, String containerName) {
        this.cosmosClient = new CosmosClientBuilder().endpoint(host).key(key).buildClient();
        this.databaseName = databaseName;
        this.containerName = containerName;

        this.cosmosAsyncClient
                = new CosmosClientBuilder()
                        .endpoint(host)
                        .key(key)
                        .consistencyLevel(ConsistencyLevel.SESSION)
                        .contentResponseOnWriteEnabled(true)
                        .buildAsyncClient();
    }

    public <T> void writeItemsToCosmosDB(List<T> items, String partitionKeyField) {
        CosmosContainer container = cosmosClient.getDatabase(databaseName).getContainer(containerName);

        for (T item : items) {
            try {
                // Assuming the item object has a getter for the partition key field
                String partitionKeyValue
                        = item.getClass()
                                .getMethod(
                                        "get"
                                        + partitionKeyField.substring(0, 1).toUpperCase()
                                        + partitionKeyField.substring(1))
                                .invoke(item)
                                .toString();

                CosmosItemResponse<T> response
                        = container.createItem(
                                item, new PartitionKey(partitionKeyValue), new CosmosItemRequestOptions());

                // Read request charge and Log request charges for slow queries:
                log.info("Read item {} charge={} RU", item, response.getRequestCharge());
                System.out.printf(
                        "Item created with request charge: %.2f RUs%n", response.getRequestCharge());

            } catch (IllegalAccessException
                    | NoSuchMethodException
                    | SecurityException
                    | InvocationTargetException e) {
                System.err.println("Error writing item to Cosmos DB: " + e.getMessage());
                // Implement more robust error handling, retries, etc.
            }
        }
    }

    public void asyncBulkUpsertsOperations(List<CosmosItemOperation> records) {
        CosmosAsyncContainer asyncContainer
                = cosmosAsyncClient.getDatabase(databaseName).getContainer(containerName);

        List<CosmosItemOperation> ops
                = records.stream()
                        .map(
                                r
                                -> CosmosBulkOperations.getUpsertItemOperation(
                                        r, new PartitionKey(r.getPartitionKeyValue())))
                        .collect(Collectors.toList());

        // Convert to Flux
        Flux<CosmosItemOperation> opsFlux = Flux.fromIterable(ops);

        // Execute bulk operations
        Flux<CosmosBulkItemResponse> responses
                = asyncContainer
                        .executeBulkOperations(opsFlux)
                        .flatMap(
                                response -> {
                                    if (response.getException() != null) {
                                        System.err.println("Failed: " + response.getException());
                                        return Flux.empty();
                                    } else {
                                        System.out.println(
                                                "Success: " + response.getResponse().getRequestCharge() + " RU");
                                        return Flux.just(response.getResponse());
                                    }
                                });
        // .blockLast(); // <-- only block in demo; in reactive apps, avoid blocking

        responses.subscribe(
                r -> {
                    log.info("Op completed statusCode={} RU={}", r.getStatusCode(), r.getRequestCharge());
                },
                err -> log.error("Bulk err", err),
                () -> log.info("Bulk done"));
    }

    public void transBatchOperationsHandleThrottling(List<CosmosItemOperation> records) {
        CosmosAsyncContainer asyncContainer
                = cosmosAsyncClient.getDatabase(databaseName).getContainer(containerName);
        try {
            /*
      TransactionalBatchResponse tr = asyncContainer.executeCosmosBatch(
          TransactionalBatch.createTransactionalBatch(new PartitionKey(pk))
              .createItemOperation(obj1)
              .upsertItemOperation(obj2)
      );
      // */
        } catch (CosmosException ex) {
            if (ex.getStatusCode() == 429) {
                log.warn("Throttled. Retry after {} ms", ex.getRetryAfterDuration().toMillis());
                Duration retryAfter = ex.getRetryAfterDuration();
                try {
                    Thread.sleep(retryAfter.toMillis());
                    // retry
                } catch (Exception ex2) {

                }
            }
        }
    }

    public static Flux<CosmosItemOperation> convertListToFlux(
            List<CosmosItemOperation> operationsList) {
        return Flux.fromIterable(operationsList);
    }

    public void close() {
        if (cosmosClient != null) {
            cosmosClient.close();
        }
    }
}
