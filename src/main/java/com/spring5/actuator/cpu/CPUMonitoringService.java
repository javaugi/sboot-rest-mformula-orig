/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.actuator.cpu;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class CPUMonitoringService {

    private final MeterRegistry meterRegistry;
    private final OperatingSystemMXBean osBean;
    private final AtomicReference<Double> processCpuUsage;
    private final AtomicReference<Double> systemCpuLoad;
    private long lastProcessCpuTime;
    private long lastSystemTime;
    private final int availableProcessors;

    public CPUMonitoringService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.osBean = ManagementFactory.getOperatingSystemMXBean();
        this.processCpuUsage = new AtomicReference<>(0.0);
        this.systemCpuLoad = new AtomicReference<>(0.0);
        this.availableProcessors = osBean.getAvailableProcessors();
        this.lastProcessCpuTime = getProcessCpuTime();
        this.lastSystemTime = System.nanoTime();
    }

    @PostConstruct
    public void init() {
        // Register custom CPU metrics
        Gauge.builder("custom.cpu.process.usage", processCpuUsage, ref -> ref.get() * 100)
                .description("Process CPU usage percentage")
                .baseUnit("percent")
                .register(meterRegistry);

        Gauge.builder("custom.cpu.system.load", systemCpuLoad, ref -> ref.get() * 100)
                .description("System CPU load percentage")
                .baseUnit("percent")
                .register(meterRegistry);

        Gauge.builder("custom.cpu.available.processors", availableProcessors, Integer::doubleValue)
                .description("Number of available processors")
                .baseUnit("cores")
                .register(meterRegistry);
    }

    @Scheduled(fixedRate = 2000) // Update every 2 seconds
    public void updateCpuMetrics() {
        calculateCpuUsage();
    }

    private long getProcessCpuTime() {
        if (osBean instanceof com.sun.management.OperatingSystemMXBean) {
            return ((com.sun.management.OperatingSystemMXBean) osBean).getProcessCpuTime();
        }
        return 0;
    }

    private void calculateCpuUsage() {
        long currentProcessCpuTime = getProcessCpuTime();
        long currentSystemTime = System.nanoTime();

        long processCpuTimeDiff = currentProcessCpuTime - lastProcessCpuTime;
        long systemTimeDiff = currentSystemTime - lastSystemTime;

        double processUsage = 0.0;
        if (systemTimeDiff > 0) {
            processUsage = (double) processCpuTimeDiff / (systemTimeDiff * availableProcessors);
        }

        double systemLoad = 0.0;
        if (osBean instanceof com.sun.management.OperatingSystemMXBean) {
            systemLoad = ((com.sun.management.OperatingSystemMXBean) osBean).getSystemCpuLoad();
        }

        processCpuUsage.set(processUsage);
        systemCpuLoad.set(systemLoad);

        lastProcessCpuTime = currentProcessCpuTime;
        lastSystemTime = currentSystemTime;
    }

    public CPUStats getCurrentCPUStats() {
        return new CPUStats(
                processCpuUsage.get() * 100,
                systemCpuLoad.get() * 100,
                availableProcessors,
                osBean.getSystemLoadAverage()
        );
    }

    public static class CPUStats {

        private final double processCpuUsage;
        private final double systemCpuLoad;
        private final int availableProcessors;
        private final double systemLoadAverage;

        public CPUStats(double processCpuUsage, double systemCpuLoad,
                int availableProcessors, double systemLoadAverage) {
            this.processCpuUsage = processCpuUsage;
            this.systemCpuLoad = systemCpuLoad;
            this.availableProcessors = availableProcessors;
            this.systemLoadAverage = systemLoadAverage;
        }

        // Getters
        public double getProcessCpuUsage() {
            return processCpuUsage;
        }

        public double getSystemCpuLoad() {
            return systemCpuLoad;
        }

        public int getAvailableProcessors() {
            return availableProcessors;
        }

        public double getSystemLoadAverage() {
            return systemLoadAverage;
        }
    }
}
