/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.actuator.gc;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class AdvancedGCMonitorService {

    private final AtomicLong lastGcCount = new AtomicLong(0);
    private final AtomicLong lastGcTime = new AtomicLong(0);
    private long lastUpdateTime = System.currentTimeMillis();

    @Scheduled(fixedRate = 5000) // Run every 5 seconds
    public void monitorGC() {
        List<GarbageCollectorMXBean> gcBeans = ManagementFactory.getGarbageCollectorMXBeans();
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();

        long totalGcCount = 0;
        long totalGcTime = 0;

        for (GarbageCollectorMXBean gcBean : gcBeans) {
            totalGcCount += gcBean.getCollectionCount();
            totalGcTime += gcBean.getCollectionTime();

            System.out.printf("GC: %s, Count: %d, Time: %dms%n",
                    gcBean.getName(), gcBean.getCollectionCount(), gcBean.getCollectionTime());
        }

        long currentTime = System.currentTimeMillis();
        long timeWindow = currentTime - lastUpdateTime;

        // Calculate GC rate
        long gcCountDelta = totalGcCount - lastGcCount.get();
        double gcRate = (timeWindow > 0) ? (gcCountDelta * 60000.0 / timeWindow) : 0; // GCs per minute

        System.out.printf("Total GC Count: %d, Total GC Time: %dms, GC Rate: %.2f per minute%n",
                totalGcCount, totalGcTime, gcRate);

        // Monitor memory pools
        for (MemoryPoolMXBean pool : ManagementFactory.getMemoryPoolMXBeans()) {
            System.out.printf("Memory Pool: %s, Usage: %d / %d%n",
                    pool.getName(),
                    pool.getUsage().getUsed(),
                    pool.getUsage().getMax());
        }

        lastGcCount.set(totalGcCount);
        lastGcTime.set(totalGcTime);
        lastUpdateTime = currentTime;
    }

    public GCHealthStatus getGCHealthStatus() {
        List<GarbageCollectorMXBean> gcBeans = ManagementFactory.getGarbageCollectorMXBeans();
        long totalRecentGcTime = 0;

        for (GarbageCollectorMXBean gcBean : gcBeans) {
            totalRecentGcTime += gcBean.getCollectionTime() - lastGcTime.get();
        }

        // Simple health check - if GC time is more than 50% of the time window, it might be problematic
        long timeWindow = System.currentTimeMillis() - lastUpdateTime;
        double gcTimeRatio = (timeWindow > 0) ? (double) totalRecentGcTime / timeWindow : 0;

        return new GCHealthStatus(
                gcTimeRatio < 0.1 ? "HEALTHY"
                        : gcTimeRatio < 0.5 ? "WARNING" : "CRITICAL",
                gcTimeRatio
        );
    }

    public static class GCHealthStatus {

        private final String status;
        private final double gcTimeRatio;

        public GCHealthStatus(String status, double gcTimeRatio) {
            this.status = status;
            this.gcTimeRatio = gcTimeRatio;
        }

        // getters
        public String getStatus() {
            return status;
        }

        public double getGcTimeRatio() {
            return gcTimeRatio;
        }
    }
}
