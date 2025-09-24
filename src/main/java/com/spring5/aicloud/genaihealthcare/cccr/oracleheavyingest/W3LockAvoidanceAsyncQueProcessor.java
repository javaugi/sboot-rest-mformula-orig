/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.aicloud.genaihealthcare.cccr.oracleheavyingest;

import com.spring5.aicloud.genaihealthcare.cccr.Claim;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;
import oracle.jdbc.OracleCallableStatement;
import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;
//import oracle.sql.ArrayDescriptor;
//import org.apache.spark.sql.execution.columnar.ARRAY;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;

/**
 *
 * @author javau
 */
public class W3LockAvoidanceAsyncQueProcessor {
    @Autowired
    private DataSource dataSource;

    public void enqueueClaims(List<Claim> claims) throws SQLException {
        String sql = """
            DECLARE
                enqueue_options DBMS_AQ.ENQUEUE_OPTIONS_T;
                message_properties DBMS_AQ.MESSAGE_PROPERTIES_T;
                message_handle RAW(16);
                message CLAIM_MESSAGE_T;
            BEGIN
                FOR claim_rec IN (SELECT * FROM TABLE(?)) LOOP
                    message := CLAIM_MESSAGE_T(
                        claim_rec.claim_id,
                        claim_rec.patient_id,
                        claim_rec.amount
                    );
                    DBMS_AQ.ENQUEUE(
                        queue_name => 'CLAIM_QUEUE',
                        enqueue_options => enqueue_options,
                        message_properties => message_properties,
                        payload => message,
                        msgid => message_handle
                    );
                END LOOP;
                COMMIT;
            END;
            """;

        try (Connection conn = dataSource.getConnection(); OracleCallableStatement stmt = (OracleCallableStatement) conn.prepareCall(sql)) {

            ArrayDescriptor desc = ArrayDescriptor.createDescriptor(
                "CLAIM_ARRAY", conn);
            ARRAY claimArray = new ARRAY(desc, conn, claims.toArray());

            stmt.setArray(1, claimArray);
            stmt.execute();
        }
    }

    @JmsListener(destination = "CLAIM_QUEUE")
    public void processClaimFromQueue(ClaimMessage message) {
        // Process individual claim without table locks
        processSingleClaim(message.getClaimId());
    }

    private void processSingleClaim(Long claimId) {

    }
}
