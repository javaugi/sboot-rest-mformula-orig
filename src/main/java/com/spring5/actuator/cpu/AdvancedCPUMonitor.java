/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.actuator.cpu;

import com.spring5.actuator.InstanceIdentifier;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.lang.management.ManagementFactory;
//import java.lang.management.OperatingSystemMXBean;
import com.sun.management.OperatingSystemMXBean;
import java.lang.management.ThreadMXBean;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class AdvancedCPUMonitor {

    private final String instanceId;
    private final MeterRegistry meterRegistry;
    private final ThreadMXBean threadBean;
    private final ConcurrentHashMap<Long, ThreadCPUMonitor> threadMonitors;
    private final AtomicLong totalProcessCpuTime;
    private final InstanceIdentifier instId;
    private final OperatingSystemMXBean osBean;
    private final AtomicReference<Instant> lastUpdate = new AtomicReference<>(Instant.now());
    private final AtomicReference<Long> lastProcessCpuTime = new AtomicReference<>(0L);

    public AdvancedCPUMonitor(MeterRegistry meterRegistry, InstanceIdentifier instId) {
        this.meterRegistry = meterRegistry;
        this.instId = instId;
        this.instanceId = instId.getInstanceId();
        this.threadBean = ManagementFactory.getThreadMXBean();
        this.threadMonitors = new ConcurrentHashMap<>();
        this.totalProcessCpuTime = new AtomicLong(0);
        this.osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);

        if (threadBean.isThreadCpuTimeSupported()) {
            threadBean.setThreadCpuTimeEnabled(true);
        }

        initializeMetrics();
        registerCPUMetrics();
    }

    private void initializeMetrics() {
        lastProcessCpuTime.set(osBean.getProcessCpuTime());
        lastUpdate.set(Instant.now());
    }

    private void registerCPUMetrics() {
        // System-wide CPU usage
        Gauge.builder("system.cpu.usage", osBean,
                bean -> bean.getCpuLoad() * 100)
                .description("System CPU Usage Percentage")
                .tags("instance", instanceId, "scope", "system")
                .baseUnit("percent")
                .register(meterRegistry);

        // Process CPU usage
        Gauge.builder("process.cpu.usage", osBean,
                bean -> bean.getProcessCpuLoad() * 100)
                .description("Process CPU Usage Percentage")
                .tags("instance", instanceId, "scope", "process")
                .baseUnit("percent")
                .register(meterRegistry);

        // Process CPU time
        Gauge.builder("process.cpu.time", osBean,
                OperatingSystemMXBean::getProcessCpuTime)
                .description("Process CPU Time in nanoseconds")
                .tags("instance", instanceId)
                .baseUnit("nanoseconds")
                .register(meterRegistry);

        // Available processors
        Gauge.builder("system.cpu.count", osBean,
                OperatingSystemMXBean::getAvailableProcessors)
                .description("Number of available processors")
                .tags("instance", instanceId)
                .register(meterRegistry);

        // System load average
        Gauge.builder("system.load.average.1m", osBean,
                OperatingSystemMXBean::getSystemLoadAverage)
                .description("System load average for last minute")
                .tags("instance", instanceId, "period", "1m")
                .register(meterRegistry);
    }

    @Scheduled(fixedRate = 5000)
    public void monitorThreadCPU() {
        long[] threadIds = threadBean.getAllThreadIds();
        long totalCpuTime = 0;

        for (long threadId : threadIds) {
            long cpuTime = threadBean.getThreadCpuTime(threadId);
            if (cpuTime != -1) {
                totalCpuTime += cpuTime;

                ThreadCPUMonitor monitor = threadMonitors.computeIfAbsent(
                        threadId, id -> new ThreadCPUMonitor(id, threadBean.getThreadInfo(id).getThreadName())
                );

                monitor.update(cpuTime);
            }
        }

        totalProcessCpuTime.set(totalCpuTime);
        updateMetrics();
    }

    private void updateMetrics() {
        // Register gauge for total process CPU time
        Gauge.builder("custom.cpu.thread.total.time", totalProcessCpuTime, AtomicLong::get)
                .description("Total CPU time consumed by all threads")
                .baseUnit("nanoseconds")
                .register(meterRegistry);
    }

    public ThreadCPUStats getThreadCPUStats(long threadId) {
        ThreadCPUMonitor monitor = threadMonitors.get(threadId);
        return monitor != null ? monitor.getStats() : null;
    }

    private static class ThreadCPUMonitor {

        private final long threadId;
        private final String threadName;
        private long lastCpuTime;
        private long lastUpdateTime;
        private double cpuUsage;

        public ThreadCPUMonitor(long threadId, String threadName) {
            this.threadId = threadId;
            this.threadName = threadName;
            this.lastCpuTime = 0;
            this.lastUpdateTime = System.nanoTime();
            this.cpuUsage = 0.0;
        }

        public void update(long currentCpuTime) {
            long currentTime = System.nanoTime();
            long timeDiff = currentTime - lastUpdateTime;
            long cpuTimeDiff = currentCpuTime - lastCpuTime;

            if (timeDiff > 0) {
                cpuUsage = (double) cpuTimeDiff / timeDiff;
            }

            lastCpuTime = currentCpuTime;
            lastUpdateTime = currentTime;
        }

        public ThreadCPUStats getStats() {
            return new ThreadCPUStats(threadId, threadName, cpuUsage * 100, lastCpuTime);
        }
    }

    public static class ThreadCPUStats {

        private final long threadId;
        private final String threadName;
        private final double cpuUsage;
        private final long totalCpuTime;

        public ThreadCPUStats(long threadId, String threadName, double cpuUsage, long totalCpuTime) {
            this.threadId = threadId;
            this.threadName = threadName;
            this.cpuUsage = cpuUsage;
            this.totalCpuTime = totalCpuTime;
        }

        // Getters
        public long getThreadId() {
            return threadId;
        }

        public String getThreadName() {
            return threadName;
        }

        public double getCpuUsage() {
            return cpuUsage;
        }

        public long getTotalCpuTime() {
            return totalCpuTime;
        }
    }
}
