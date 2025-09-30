/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.aicloud.genaihealthcare.cccr.pgheavyingest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author javau
 */
public class W4PgPerfOptParallelDMLService {

    /*
  4. Performance Optimization Techniques
  Parallel DML and Query
     */
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void enableParallelDML() {
        jdbcTemplate.execute("ALTER SESSION ENABLE PARALLEL DML");
    }

    public void bulkMerge(String targetTable, String sourceTable) {
        String sql
                = String.format(
                        """
            MERGE /*+ PARALLEL(8) */ INTO %s AS t
            USING %s AS s
            ON (t.claim_id = s.claim_id)
            WHEN MATCHED THEN UPDATE SET
                t.patient_id = s.patient_id,
                t.amount = s.amount,
                t.status = s.status
            WHEN NOT MATCHED THEN INSERT
                (claim_id, patient_id, amount, status)
            VALUES
                (s.claim_id, s.patient_id, s.amount, s.status)
            LOG ERRORS REJECT LIMIT UNLIMITED
            """,
                        targetTable, sourceTable);

        jdbcTemplate.execute(sql);
    }
}
