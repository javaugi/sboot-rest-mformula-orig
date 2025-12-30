/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5;

import io.micrometer.core.instrument.MeterRegistry;
import java.net.InetAddress;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfig {

    @Bean
    public MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
        String hostname = System.getenv("HOSTNAME");
        String instanceId = System.getenv("INSTANCE_ID");
        String serverPort = "80880";
        String ipAddress = "localhost";

        try {
            if (hostname == null || hostname.isEmpty()) {
                hostname = InetAddress.getLocalHost().getHostName();
            }
            if (instanceId == null || instanceId.isEmpty()) {
                instanceId = hostname + ":" + serverPort;
            }
            ipAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (Exception ex) {

        }

        final String finInstId = instanceId;
        final String finHostname = hostname;
        final String finipAddress = ipAddress;

        return registry -> registry.config()
                .commonTags(
                        "instance", finInstId,
                        "host", finHostname,
                        "ip", finipAddress,
                        "application", "api-gateway",
                        "environment", System.getenv("ENV") != null
                        ? System.getenv("ENV") : "dev"
                );
    }
}
