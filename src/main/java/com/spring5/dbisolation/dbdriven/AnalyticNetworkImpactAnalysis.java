/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.dbdriven;

/**
 * @author javau
 */
public class AnalyticNetworkImpactAnalysis {
}

/*
Question: How does network type affect OTA update performance?

WITH network_stats AS (
    SELECT
        network_type,
        COUNT(*) AS total_updates,
        AVG(download_speed_kbps) AS avg_speed,
        PERCENTILE_CONT(0.5) WITHIN GROUP (ORDER BY download_speed_kbps) AS median_speed,
        AVG(EXTRACT(EPOCH FROM (completion_time - attempt_time))) AS avg_duration_sec,
        COUNT(*) FILTER (WHERE status = 'success') AS successes
    FROM vehicle_updates
    WHERE network_type IS NOT NULL
    GROUP BY network_type
)

SELECT
    network_type,
    total_updates,
    ROUND(avg_speed, 2) AS avg_speed_kbps,
    median_speed,
    ROUND(avg_duration_sec/60, 2) AS avg_duration_min,
    ROUND(100.0 * successes / total_updates, 2) AS success_rate,
    ROUND(total_updates * 1.0 / (SELECT SUM(total_updates) FROM network_stats) * 100, 2) AS percentage_of_total
FROM network_stats
ORDER BY success_rate DESC;
 */
