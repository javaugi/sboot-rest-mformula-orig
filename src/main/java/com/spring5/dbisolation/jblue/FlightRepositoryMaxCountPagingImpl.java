/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.jblue;

import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.models.SqlQuerySpec;
import com.azure.spring.data.cosmos.core.CosmosTemplate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class FlightRepositoryMaxCountPagingImpl implements FlightRepositoryCustomAdvancedPaging {

    private final CosmosTemplate cosmosTemplate;

    @Override
    public List<FlightEvent> findLargeDatasetWithPagination(
            String query, int maxItemCount, String continuationToken) {
        CosmosQueryRequestOptions options = new CosmosQueryRequestOptions();
        options.setMaxBufferedItemCount(maxItemCount); // Control RU spikes

        SqlQuerySpec querySpec = new SqlQuerySpec(query);

        List<FlightEvent> results = new ArrayList<>();
        String currentContinuationToken = continuationToken;

        do {
            // Execute query with pagination
            /*
      CosmosPagedIterable<FlightEvent> pagedIterable = cosmosTemplate.runQuery(
          querySpec, FlightEvent.class, FlightEvent.class);

      // Process current page
      pagedIterable.iterableByPage(currentContinuationToken).forEach(page -> {
          results.addAll(page.getResults());
          currentContinuationToken = page.getContinuationToken();
      });
      // */

        } while (currentContinuationToken != null && results.size() < 1000); // Safety limit

        return results;
    }

    @Override
    public void processLargeDatasetInParallel(String query, int batchSize, int parallelism) {
        ExecutorService executor = Executors.newFixedThreadPool(parallelism);
        List<Future<?>> futures = new ArrayList<>();

        // First, get total count to divide work
        long totalCount = getApproximateCount(query);
        long itemsPerThread = totalCount / parallelism;

        for (int i = 0; i < parallelism; i++) {
            final int threadIndex = i;
            futures.add(
                    executor.submit(
                            () -> {
                                processBatch(query, threadIndex * itemsPerThread, batchSize);
                            }));
        }

        // Wait for completion
        futures.forEach(
                future -> {
                    try {
                        future.get();
                    } catch (Exception e) {
                        // Handle exception
                    }
                });

        executor.shutdown();
    }

    private void processBatch(String baseQuery, long offset, int batchSize) {
        String query = baseQuery + " OFFSET " + offset + " LIMIT " + batchSize;
        CosmosQueryRequestOptions options = new CosmosQueryRequestOptions();
        options.setMaxBufferedItemCount(100); // Small batches to avoid RU spikes

        SqlQuerySpec querySpec = new SqlQuerySpec(query);
        Pageable pageable = Pageable.ofSize(100); // new Pageable();

        cosmosTemplate
                .runQuery(querySpec, FlightEvent.class, FlightEvent.class)
                .forEach(this::processFlight);
    }

    private long getApproximateCount(String query) {
        /*
    String countQuery = "SELECT VALUE COUNT(1) FROM (" + query + ")";
    return cosmosTemplate.runQuery(new SqlQuerySpec(countQuery), Long.class, Long.class)
        .stream()
        .findFirst()
        .orElse(0L);
    // */
        return 0L;
    }

    private void processFlight(FlightEvent flight) {
        // Process individual flight
    }
}
