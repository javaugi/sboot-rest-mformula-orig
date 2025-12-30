/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.actuator.cpu;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.util.concurrent.atomic.AtomicLong;

public class BasicCPUMonitor {

    private final OperatingSystemMXBean osBean;
    private final RuntimeMXBean runtimeBean;
    private final AtomicLong lastProcessCpuTime;
    private final AtomicLong lastSystemTime;
    private final int availableProcessors;

    public BasicCPUMonitor() {
        this.osBean = ManagementFactory.getOperatingSystemMXBean();
        this.runtimeBean = ManagementFactory.getRuntimeMXBean();
        this.availableProcessors = osBean.getAvailableProcessors();
        this.lastProcessCpuTime = new AtomicLong(getProcessCpuTime());
        this.lastSystemTime = new AtomicLong(System.nanoTime());
    }

    private long getProcessCpuTime() {
        if (osBean instanceof com.sun.management.OperatingSystemMXBean) {
            return ((com.sun.management.OperatingSystemMXBean) osBean).getProcessCpuTime();
        }
        return 0;
    }

    public double getProcessCpuUsage() {
        long currentProcessCpuTime = getProcessCpuTime();
        long currentSystemTime = System.nanoTime();

        long processCpuTimeDiff = currentProcessCpuTime - lastProcessCpuTime.get();
        long systemTimeDiff = currentSystemTime - lastSystemTime.get();

        lastProcessCpuTime.set(currentProcessCpuTime);
        lastSystemTime.set(currentSystemTime);

        if (systemTimeDiff > 0) {
            return (double) processCpuTimeDiff / (systemTimeDiff * availableProcessors);
        }

        return 0.0;
    }

    public double getSystemCpuLoad() {
        if (osBean instanceof com.sun.management.OperatingSystemMXBean) {
            return ((com.sun.management.OperatingSystemMXBean) osBean).getSystemCpuLoad();
        }
        return osBean.getSystemLoadAverage();
    }

    public void printCpuStats() {
        double processCpuUsage = getProcessCpuUsage() * 100;
        double systemCpuLoad = getSystemCpuLoad() * 100;

        System.out.printf("Process CPU Usage: %.2f%%%n", processCpuUsage);
        System.out.printf("System CPU Load: %.2f%%%n", systemCpuLoad);
        System.out.printf("Available Processors: %d%n", availableProcessors);
        System.out.printf("System Load Average: %.2f%n", osBean.getSystemLoadAverage());
    }
}
