/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.aicloud.genaihealthcare;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.Instant;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ResponsibleAIIndex {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public String operation; // e.g. "LoyaltyEnrollment"
    public String modelName;
    public double transparencyScore;
    public double fairnessScore;
    public double privacyScore;
    public double regulatoryAlignmentScore;
    private Double overallScore;
    public Instant createdAt;
    private LocalDateTime lastUpdated;
    public String notes; // JSON or text explaining scoring decisions
    private String complianceStatus;

    public Double calculateOverallScore() {
        return (transparencyScore + fairnessScore + privacyScore + regulatoryAlignmentScore) / 4;
    }

    public String calculateComplianceStatus() {
        return overallScore >= 8.0 ? "COMPLIANT" : "NEEDS_REVIEW";
    }

    public void setTransparencyScore(Double transparencyScore) {
        this.transparencyScore = transparencyScore;
        this.overallScore = calculateOverallScore();
        this.complianceStatus = calculateComplianceStatus();
    }

    public void setFairnessScore(Double fairnessScore) {
        this.fairnessScore = fairnessScore;
        this.overallScore = calculateOverallScore();
        this.complianceStatus = calculateComplianceStatus();
    }

    public void setRegulatoryAlignmentScore(Double regulatoryAlignmentScore) {
        this.regulatoryAlignmentScore = regulatoryAlignmentScore;
        this.overallScore = calculateOverallScore();
        this.complianceStatus = calculateComplianceStatus();
    }
}
