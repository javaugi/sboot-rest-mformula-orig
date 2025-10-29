package com.spring5.aicloud.gcp.dataflow;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class MetricsController {

    private final Random random = new Random();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    @GetMapping("/metrics")
    public List<Map<String, Object>> getMetrics() {
        List<Map<String, Object>> metrics = new ArrayList<>();
        
        for (int i = 0; i < 10; i++) {
            Map<String, Object> metric = new HashMap<>();
            metric.put("timestamp", LocalDateTime.now().minusMinutes(i).format(formatter));
            metric.put("responseTime", random.nextInt(100) + 50);
            metric.put("requests", random.nextInt(1000) + 500);
            metric.put("errors", random.nextInt(10));
            metrics.add(metric);
        }
        
        return metrics;
    }

    @GetMapping("/config")
    public Map<String, Object> getConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("environment", "local-development");
        config.put("version", "1.0.0");
        config.put("features", Arrays.asList("metrics-collection", "real-time-dashboard"));
        config.put("lastUpdated", LocalDateTime.now().toString());
        return config;
    }

    @PostMapping("/logs")
    public ResponseEntity<String> sendLog(@RequestBody Map<String, Object> logData) {
        // In production, this would publish to Pub/Sub
        System.out.println("Received log: " + logData);
        return ResponseEntity.ok("Log received successfully");
    }

    @PostMapping("/metrics")
    public ResponseEntity<String> sendMetric(@RequestBody Map<String, Object> metricData) {
        // In production, this would publish to Pub/Sub
        System.out.println("Received metric: " + metricData);
        return ResponseEntity.ok("Metric received successfully");
    }
}