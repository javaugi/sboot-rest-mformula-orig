/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.dbdriven;

/**
 *
 * @author javau
 */
public class AnalyticBatteryOptimization4Updates {
    
}
/*
Question: Analyze the impact of battery level on update success rates.

SELECT
    CASE
        WHEN battery_level >= 80 THEN '80-100%'
        WHEN battery_level >= 60 THEN '60-79%'
        WHEN battery_level >= 40 THEN '40-59%'
        WHEN battery_level >= 20 THEN '20-39%'
        ELSE '0-19%'
    END AS battery_range,
    COUNT(*) AS total_attempts,
    COUNT(*) FILTER (WHERE status = 'success') AS successes,
    COUNT(*) FILTER (WHERE status = 'failed') AS failures,
    ROUND(100.0 * COUNT(*) FILTER (WHERE status = 'success') / COUNT(*), 2) AS success_rate,
    ROUND(AVG(download_speed_kbps), 2) AS avg_speed,
    ROUND(AVG(EXTRACT(EPOCH FROM (completion_time - attempt_time)))/60, 2) AS avg_duration_min
FROM vehicle_updates
WHERE battery_level IS NOT NULL
GROUP BY battery_range
ORDER BY battery_range;
*/