/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.jblue;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Component
public class CentralizedLoggingCorrelationIdFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String correlationId = UUID.randomUUID().toString();
        MDC.put("correlationId", correlationId);
        try {
            chain.doFilter(request, response);
        } finally {
            MDC.remove("correlationId");
        }
    }
}

/*
In org.slf4j.MDC, MDC stands for Mapped Diagnostic Context.
It is a feature used in logging frameworks like SLF4J (Simple Logging Facade for Java) to enrich log messages with contextual information. This
    context is typically stored on a per-thread basis, allowing you to associate specific data (like a user ID, session ID, or transaction ID)
    with all log messages generated within that thread's execution. This is particularly useful in multi-threaded environments, such as web applications
    or microservices, where logs from different operations can be interleaved, making it difficult to trace a single request or process. By using MDC,
    you can easily filter, group, and analyze log entries based on this contextual data.
 */
// 2. Logging with JSON + Correlation IDs
// Use SLF4J + Logback for structured logs.
// Add logback-spring.xml in src/main/resources

/*
2. Logging with JSON + Correlation IDs

Use SLF4J + Logback for structured logs.

Add logback-spring.xml in src/main/resources
<configuration>
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder class="net.logstash.logback.encoder.LoggingEventCompositeJsonEncoder">
            <providers>
                <timestamp/>
                <logLevel/>
                <threadName/>
                <loggerName/>
                <message/>
                <mdc/>
                <context/>
            </providers>
        </encoder>
    </appender>

    <root level="INFO">
        <appender-ref ref="STDOUT"/>
    </root>
</configuration>

Add Correlation ID Filter using this class
 */
