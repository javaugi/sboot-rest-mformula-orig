/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.aicloud.genaihealthcare.cccr.oracleheavyingest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author javau
 */
public class W4PerfOptPartitionExchangeService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void loadViaExchangePartition(
            String stagingTable, String targetTable, String partitionName) {

        // 1. Load data into staging table (no indexes)
        loadStagingTable(stagingTable);

        // 2. Create matching indexes on staging
        createStagingIndexes(stagingTable);

        // 3. Exchange partition
        String exchangeSql
                = String.format(
                        """
            ALTER TABLE %s EXCHANGE PARTITION %s
            WITH TABLE %s INCLUDING INDEXES
            """,
                        targetTable, partitionName, stagingTable);

        jdbcTemplate.execute(exchangeSql);

        // 4. Rebuild global indexes if needed
        rebuildGlobalIndexes(targetTable);
    }

    private void loadStagingTable(String stagingTable) {
        // Use direct path load into staging
        String sql
                = String.format(
                        """
            INSERT /*+ APPEND */ INTO %s
            SELECT * FROM external_claims
            """,
                        stagingTable);

        jdbcTemplate.execute(sql);
    }

    private void createStagingIndexes(String stagingTable) {
    }

    private void rebuildGlobalIndexes(String targetTable) {
    }
}
