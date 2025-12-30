/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.actuator.cpu;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/cpu")
public class CPUMetricsController {

    private final CPUMonitoringService cpuMonitoringService;
    private final AdvancedCPUMonitor advancedCPUMonitor;

    public CPUMetricsController(CPUMonitoringService cpuMonitoringService,
            AdvancedCPUMonitor advancedCPUMonitor) {
        this.cpuMonitoringService = cpuMonitoringService;
        this.advancedCPUMonitor = advancedCPUMonitor;
    }

    @GetMapping("/stats")
    public Map<String, Object> getCPUStats() {
        CPUMonitoringService.CPUStats stats = cpuMonitoringService.getCurrentCPUStats();

        Map<String, Object> response = new HashMap<>();
        response.put("processCpuUsage", String.format("%.2f%%", stats.getProcessCpuUsage()));
        response.put("systemCpuLoad", String.format("%.2f%%", stats.getSystemCpuLoad()));
        response.put("availableProcessors", stats.getAvailableProcessors());
        response.put("systemLoadAverage", String.format("%.2f", stats.getSystemLoadAverage()));
        response.put("timestamp", System.currentTimeMillis());

        return response;
    }

    @GetMapping("/health")
    public Map<String, String> getCPUHealth() {
        CPUMonitoringService.CPUStats stats = cpuMonitoringService.getCurrentCPUStats();

        String status;
        if (stats.getProcessCpuUsage() > 80) {
            status = "CRITICAL";
        } else if (stats.getProcessCpuUsage() > 60) {
            status = "WARNING";
        } else {
            status = "HEALTHY";
        }

        Map<String, String> health = new HashMap<>();
        health.put("status", status);
        health.put("currentUsage", String.format("%.2f%%", stats.getProcessCpuUsage()));
        health.put("message", getHealthMessage(status));

        return health;
    }

    private String getHealthMessage(String status) {
        switch (status) {
            case "CRITICAL":
                return "CPU usage is very high. Consider optimizing or scaling.";
            case "WARNING":
                return "CPU usage is elevated. Monitor closely.";
            default:
                return "CPU usage is within normal limits.";
        }
    }
}
