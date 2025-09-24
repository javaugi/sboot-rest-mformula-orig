-- Example PL/SQL for CCCR Data Analysis
CREATE OR REPLACE PACKAGE CCCR_ANALYSIS_PKG AS
    PROCEDURE calculate_savings_by_provider(
        p_start_date IN DATE,
        p_end_date IN DATE
    );
    
    FUNCTION get_cross_claim_opportunities(
        p_provider_id IN VARCHAR2
    ) RETURN SYS_REFCURSOR;
    
END CCCR_ANALYSIS_PKG;

CREATE OR REPLACE PACKAGE BODY CCCR_ANALYSIS_PKG AS

    PROCEDURE calculate_savings_by_provider(
        p_start_date IN DATE,
        p_end_date IN DATE
    ) IS
    BEGIN
        MERGE INTO provider_savings ps
        USING (
            SELECT 
                provider_id,
                SUM(CASE WHEN review_type = 'CCCR' THEN estimated_savings ELSE 0 END) AS cccr_savings,
                SUM(CASE WHEN review_type = 'STANDARD' THEN estimated_savings ELSE 0 END) AS standard_savings
            FROM clinical_claims
            WHERE process_date BETWEEN p_start_date AND p_end_date
            GROUP BY provider_id
        ) calc
        ON (ps.provider_id = calc.provider_id)
        WHEN MATCHED THEN
            UPDATE SET 
                ps.cccr_savings = calc.cccr_savings,
                ps.standard_savings = calc.standard_savings,
                ps.last_updated = SYSDATE
        WHEN NOT MATCHED THEN
            INSERT (provider_id, cccr_savings, standard_savings, last_updated)
            VALUES (calc.provider_id, calc.cccr_savings, calc.standard_savings, SYSDATE);
    END calculate_savings_by_provider;
    
    FUNCTION get_cross_claim_opportunities(
        p_provider_id IN VARCHAR2
    ) RETURN SYS_REFCURSOR IS
        v_cursor SYS_REFCURSOR;
    BEGIN
        OPEN v_cursor FOR
            SELECT 
                c.claim_id,
                c.patient_id,
                c.procedure_code,
                c.submit_date,
                EXISTS (
                    SELECT 1 FROM clinical_claims cc
                    WHERE cc.patient_id = c.patient_id
                    AND cc.provider_id != p_provider_id
                    AND cc.service_date BETWEEN c.service_date - 30 AND c.service_date + 30
                ) AS has_cross_claim
            FROM clinical_claims c
            WHERE c.provider_id = p_provider_id
            AND c.status = 'PENDING'
            ORDER BY c.submit_date DESC;
        
        RETURN v_cursor;
    END get_cross_claim_opportunities;
    
END CCCR_ANALYSIS_PKG;
