/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.aicloud.genaihealthcare.cccr.oracleheavyingest;

//import jakarta.activation.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 *
 * @author javau
 */
public class W2MinLockingRowIdBatchProcessor {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private DataSource dataSource;

    public void processByRowIdBatches(String tableName, int batchSize) throws SQLException {
        String baseQuery = "SELECT ROWID FROM " + tableName
            + " WHERE process_status = 'PENDING'";

        try (Connection conn = dataSource.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(baseQuery)) {

            List<String> rowIds = new ArrayList<>();
            while (rs.next()) {
                rowIds.add(rs.getString(1));
                if (rowIds.size() >= batchSize) {
                    processBatch(rowIds, tableName);
                    rowIds.clear();
                }
            }
            if (!rowIds.isEmpty()) {
                processBatch(rowIds, tableName);
            }
        }
    }

    private void processBatch(List<String> rowIds, String tableName) {
        String rowIdList = rowIds.stream()
            .map(rid -> "'" + rid.replace("'", "''") + "'")
            .collect(Collectors.joining(","));

        String updateSql = String.format("""
            UPDATE %s
            SET process_status = 'PROCESSED',
                process_date = SYSDATE
            WHERE ROWID IN (%s)
            """, tableName, rowIdList);

        jdbcTemplate.update(updateSql);
    }
}
