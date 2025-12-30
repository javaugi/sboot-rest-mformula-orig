/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.jmeter;

public class LoadTestResult {

    private double averageResponseTime;
    private double errorPercentage;
    private double throughput;
    private long totalRequests;
    private long failedRequests;

    // getters and setters
    public double getAverageResponseTime() {
        return averageResponseTime;
    }

    public void setAverageResponseTime(double averageResponseTime) {
        this.averageResponseTime = averageResponseTime;
    }

    public double getErrorPercentage() {
        return errorPercentage;
    }

    public void setErrorPercentage(double errorPercentage) {
        this.errorPercentage = errorPercentage;
    }

    public double getThroughput() {
        return throughput;
    }

    public void setThroughput(double throughput) {
        this.throughput = throughput;
    }

    public long getTotalRequests() {
        return totalRequests;
    }

    public void setTotalRequests(long totalRequests) {
        this.totalRequests = totalRequests;
    }

    public long getFailedRequests() {
        return failedRequests;
    }

    public void setFailedRequests(long failedRequests) {
        this.failedRequests = failedRequests;
    }
}
