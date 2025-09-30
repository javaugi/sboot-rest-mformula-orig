/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.wmart.log;

public class LogEntry {

    private final String message;
    private final LogLevel level;
    private final String timestamp;
    private final String serviceName;
    private final String threadId;

    public LogEntry(
            String message, LogLevel level, String timestamp, String serviceName, String threadId) {
        this.message = message;
        this.level = level;
        this.timestamp = timestamp;
        this.serviceName = serviceName;
        this.threadId = threadId;
    }

    // Getters
    public String getMessage() {
        return message;
    }

    public LogLevel getLevel() {
        return level;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getThreadId() {
        return threadId;
    }

    public boolean isError() {
        return level == LogLevel.ERROR;
    }
}
