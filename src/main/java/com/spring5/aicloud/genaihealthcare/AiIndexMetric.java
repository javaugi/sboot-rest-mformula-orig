/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.aicloud.genaihealthcare;

/**
 * Represents a single metric tracked in the Responsible AI Index.
 */
class AiIndexMetric {

    public String metricName;
    public double score;
    public String explanation;

    public AiIndexMetric(String metricName, double score, String explanation) {
        this.metricName = metricName;
        this.score = score;
        this.explanation = explanation;
    }

    // Getters
    public String getMetricName() {
        return metricName;
    }

    public double getScore() {
        return score;
    }

    public String getExplanation() {
        return explanation;
    }
}
