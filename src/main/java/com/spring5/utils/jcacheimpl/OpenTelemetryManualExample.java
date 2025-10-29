/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.utils.jcacheimpl;

//import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;
import io.opentelemetry.exporter.jaeger.JaegerGrpcSpanExporter; // Or other exporter like OTLP
//import org.springframework.boot.actuate.autoconfigure.tracing.TracingProperties.OpenTelemetry;

public class OpenTelemetryManualExample {

    private static final String SERVICE_NAME = "my-java-app";
    private static final String SERVICE_VERSION = "1.0.0";
    private static final String JAEGER_ENDPOINT = "http://localhost:14250"; // Adjust as needed

    public static void main(String[] args) {
        // 1. Configure and initialize OpenTelemetry SDK
        JaegerGrpcSpanExporter jaegerExporter = JaegerGrpcSpanExporter.builder()
                .setEndpoint(JAEGER_ENDPOINT)
                .build();

        SdkTracerProvider tracerProvider = SdkTracerProvider.builder()
                .addSpanProcessor(SimpleSpanProcessor.create(jaegerExporter))
                .build();

        OpenTelemetrySdk.builder()
                .setTracerProvider(tracerProvider)
                .buildAndRegisterGlobal(); // Register as global OpenTelemetry instance

        // 2. Get a Tracer instance
        Tracer tracer = OpenTelemetry.noop().getTracer(SERVICE_NAME, SERVICE_VERSION);
        //Tracer tracer2 = OpenTelemetry.getGlobalOpenTelemetry().getTracer(SERVICE_NAME);

        // 3. Create and manage a Span
        Span span = tracer.spanBuilder("my-manual-operation")
                .startSpan();
        try {
            // Simulate some work
            System.out.println("Performing some work within the traced operation...");
            Thread.sleep(100); // Simulate delay
            span.setAttribute("custom.attribute", "value"); // Add attributes to the span
        } catch (InterruptedException e) {
            span.recordException(e); // Record exceptions
            span.setStatus(io.opentelemetry.api.trace.StatusCode.ERROR, "Operation failed");
            Thread.currentThread().interrupt();
        } finally {
            span.end(); // End the span
        }

        System.out.println("Operation completed and trace sent (if exporter is configured).");

        // Ensure the exporter has time to send data before application exits
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
