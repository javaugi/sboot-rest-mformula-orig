/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.aicloud.genaihealthcare.cccr.oracleheavyingest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 *
 * @author javau
 */
public class W2MinLockingPartitionedBatchProcessor {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private TaskExecutor taskExecutor;

    public void processLargeTableInPartitions(String tableName,
        String partitionKey,
        int batchSize) {

        // Get min and max partition keys
        Long minId = jdbcTemplate.queryForObject(
            "SELECT MIN(" + partitionKey + ") FROM " + tableName, Long.class);
        Long maxId = jdbcTemplate.queryForObject(
            "SELECT MAX(" + partitionKey + ") FROM " + tableName, Long.class);

        // Process in parallel partitions
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (long start = minId; start <= maxId; start += batchSize) {
            long finalStart = start;
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                processPartition(tableName, partitionKey, finalStart, finalStart + batchSize);
            }, taskExecutor);
            futures.add(future);
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    private void processPartition(String tableName, String partitionKey,
        long start, long end) {
        String sql = String.format("""
            UPDATE /*+ PARALLEL(4) */ %s
            SET process_status = 'PROCESSED',
                process_date = SYSDATE
            WHERE %s BETWEEN ? AND ?
            AND process_status = 'PENDING'
            """, tableName, partitionKey);

        jdbcTemplate.update(sql, start, end);
    }
}
