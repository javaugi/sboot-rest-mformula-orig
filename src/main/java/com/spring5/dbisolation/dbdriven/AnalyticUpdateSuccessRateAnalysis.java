/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.dbdriven;

/**
 * @author javau
 */
public class AnalyticUpdateSuccessRateAnalysis {
}

/*
Question: Write a query to analyze OTA update success rates by vehicle model and region.

WITH update_stats AS (
    SELECT
        v.model_name,
        v.region,
        COUNT(*) AS total_attempts,
        COUNT(*) FILTER (WHERE vu.status = 'success') AS successful_updates,
        COUNT(*) FILTER (WHERE vu.status = 'failed') AS failed_updates,
        AVG(EXTRACT(EPOCH FROM (vu.completion_time - vu.attempt_time))/60 AS avg_duration_minutes,
        PERCENTILE_CONT(0.5) WITHIN GROUP (ORDER BY vu.download_speed_kbps) AS median_download_speed
    FROM vehicle_updates vu
    JOIN vehicles v ON vu.vehicle_id = v.vehicle_id
    WHERE vu.attempt_time >= NOW() - INTERVAL '30 days'
    GROUP BY GROUPING SETS (
        (v.model_name, v.region),
        (v.model_name),
        (v.region),
        ()
    )
)
SELECT
    COALESCE(model_name, 'All Models') AS model_name,
    COALESCE(region, 'All Regions') AS region,
    total_attempts,
    ROUND(100.0 * successful_updates / NULLIF(total_attempts, 0), 2) AS success_rate,
    ROUND(avg_duration_minutes, 2) AS avg_duration_min,
    median_download_speed
FROM update_stats
ORDER BY
    CASE WHEN model_name IS NULL THEN 1 ELSE 0 END,
    CASE WHEN region IS NULL THEN 1 ELSE 0 END,
    success_rate DESC;
 */
