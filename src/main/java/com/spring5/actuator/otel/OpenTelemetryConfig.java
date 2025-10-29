/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.actuator.otel;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import io.opentelemetry.semconv.ResourceAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*
Testing the Application
Start your Spring Boot application

Make requests to: see OTelDemoController
    http://localhost:8088/otelapi/hello/John
    http://localhost:8088/otelapi/users
    View traces in Jaeger UI: http://localhost:16686

Key Points:
    Automatic Instrumentation: Spring Boot automatically creates spans for HTTP requests
    Manual Instrumentation: Use Tracer to create custom spans for business logic
    Span Attributes: Add contextual information to spans
    Nested Spans: Create parent-child relationships for complex operations
    Error Handling: Record exceptions in spans

This example shows both automatic instrumentation (provided by Spring Boot Starter) and manual instrumentation for custom business logic.
 */

@Configuration
public class OpenTelemetryConfig {

    @Bean
    public OpenTelemetry openTelemetry() {
        Resource resource = Resource.getDefault()
                .merge(Resource.create(Attributes.of(
                        ResourceAttributes.SERVICE_NAME, "my-spring-boot-app",
                        ResourceAttributes.DEPLOYMENT_ENVIRONMENT, "development"
                )));

        SdkTracerProvider tracerProvider = SdkTracerProvider.builder()
                .addSpanProcessor(BatchSpanProcessor.builder(
                        OtlpGrpcSpanExporter.builder()
                                .setEndpoint("http://localhost:4317")
                                .build()
                ).build())
                .setResource(resource)
                .build();

        return OpenTelemetrySdk.builder()
                .setTracerProvider(tracerProvider)
                .buildAndRegisterGlobal();
    }
}
