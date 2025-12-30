/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.actuator.gc;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class GCMetricsController {

    private final GCMonitor gcMonitor;
    private final AdvancedGCMonitorService advancedGCMonitor;

    public GCMetricsController(GCMonitor gcMonitor, AdvancedGCMonitorService advancedGCMonitor) {
        this.gcMonitor = gcMonitor;
        this.advancedGCMonitor = advancedGCMonitor;
    }

    @GetMapping("/api/gc/metrics")
    public Map<String, Object> getGCMetrics() {
        Map<String, Object> metrics = new HashMap<>();

        // Basic GC metrics
        metrics.put("basic", gcMonitor.getGCStats());

        // Health status
        metrics.put("health", advancedGCMonitor.getGCHealthStatus());

        // Detailed GC info
        List<Map<String, Object>> gcDetails = new ArrayList<>();
        for (GarbageCollectorMXBean gcBean : ManagementFactory.getGarbageCollectorMXBeans()) {
            Map<String, Object> gcInfo = new HashMap<>();
            gcInfo.put("name", gcBean.getName());
            gcInfo.put("collectionCount", gcBean.getCollectionCount());
            gcInfo.put("collectionTime", gcBean.getCollectionTime());
            gcInfo.put("memoryPoolNames", gcBean.getMemoryPoolNames());
            gcDetails.add(gcInfo);
        }
        metrics.put("detailed", gcDetails);

        return metrics;
    }
}
