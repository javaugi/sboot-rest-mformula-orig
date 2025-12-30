/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.actuator.gc;

import com.spring5.actuator.InstanceIdentifier;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class GCMetrics {

    private final List<GarbageCollectorMXBean> gcBeans;
    private final String instanceId;
    private final MeterRegistry meterRegistry;
    private final InstanceIdentifier instId;

    public GCMetrics(MeterRegistry meterRegistry, InstanceIdentifier instId) {
        this.meterRegistry = meterRegistry;
        this.instId = instId;
        this.instanceId = instId.getInstanceId();
        this.gcBeans = ManagementFactory.getGarbageCollectorMXBeans();

        registerGCMetrics();
    }

    private void registerGCMetrics() {
        for (GarbageCollectorMXBean gcBean : gcBeans) {
            String gcName = gcBean.getName();

            Gauge.builder("jvm.gc.collection.count", gcBean,
                    bean -> bean.getCollectionCount())
                    .description("GC collection count")
                    .tags("gc", gcName, "instance", instanceId)
                    .register(meterRegistry);

            Gauge.builder("jvm.gc.collection.time", gcBean,
                    bean -> bean.getCollectionTime())
                    .description("GC collection time in milliseconds")
                    .tags("gc", gcName, "instance", instanceId)
                    .register(meterRegistry);
        }
    }
}
