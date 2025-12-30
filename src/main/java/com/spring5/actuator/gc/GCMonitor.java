/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.actuator.gc;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Gauge;
import org.springframework.stereotype.Component;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;

@Component
public class GCMonitor {

    private final MeterRegistry meterRegistry;
    private final Map<String, GarbageCollectorMXBean> gcBeans = new HashMap<>();

    public GCMonitor(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        initializeGCMonitoring();
    }

    private void initializeGCMonitoring() {
        // Get all garbage collector MXBeans
        ManagementFactory.getClassLoadingMXBean();
        ManagementFactory.getCompilationMXBean();
        ManagementFactory.getMemoryMXBean();
        ManagementFactory.getMemoryPoolMXBeans();

        for (GarbageCollectorMXBean gcBean : ManagementFactory.getGarbageCollectorMXBeans()) {
            gcBeans.put(gcBean.getName(), gcBean);

            // Register GC metrics with Micrometer
            Gauge.builder("jvm.gc.collection.count", gcBean, GarbageCollectorMXBean::getCollectionCount)
                    .tag("gc", gcBean.getName())
                    .description("Total number of collections that have occurred")
                    .register(meterRegistry);

            Gauge.builder("jvm.gc.collection.time", gcBean, GarbageCollectorMXBean::getCollectionTime)
                    .tag("gc", gcBean.getName())
                    .description("Approximate accumulated collection elapsed time in milliseconds")
                    .register(meterRegistry);
        }
    }

    // Method to get current GC stats
    public Map<String, GCStats> getGCStats() {
        Map<String, GCStats> stats = new HashMap<>();
        for (GarbageCollectorMXBean gcBean : gcBeans.values()) {
            stats.put(gcBean.getName(),
                    new GCStats(gcBean.getCollectionCount(), gcBean.getCollectionTime()));
        }
        return stats;
    }

    public static class GCStats {

        private final long collectionCount;
        private final long collectionTime;

        public GCStats(long collectionCount, long collectionTime) {
            this.collectionCount = collectionCount;
            this.collectionTime = collectionTime;
        }

        // getters
        public long getCollectionCount() {
            return collectionCount;
        }

        public long getCollectionTime() {
            return collectionTime;
        }
    }

    protected void builtInJvmMetrics() {
        /*
        2. Using Micrometer's Built-in JVM Metrics
        Spring Boot Actuator with Micrometer already provides many GC metrics automatically:
        yaml
        # application.yml
        management:
          endpoints:
            web:
              exposure:
                include: metrics,prometheus
          metrics:
            export:
              prometheus:
                enabled: true
            tags:
              application: my-spring-app

                The automatically available GC metrics include:
                jvm.gc.max.data.size - Max size of old generation memory pool
                jvm.gc.live.data.size - Size of old generation memory pool after a full GC
                jvm.gc.memory.allocated - Incremented for an increase in the size of the young generation memory pool after one GC to before the next
                jvm.gc.memory.promoted - Count of positive increases in the size of the old generation memory pool before GC to after GC        
         */

    }
}
