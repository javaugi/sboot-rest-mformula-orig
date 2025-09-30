/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.aicloud.genaihealthcare;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
public class LoyaltyProgram {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "patient_id", nullable = false)
    private String patientId;

    @Column(name = "program_name", nullable = false)
    private String programName;

    @Column(name = "enrollment_date")
    private LocalDateTime enrollmentDate;

    @Builder.Default
    @Column(name = "points_balance")
    private Integer pointsBalance = 0;

    @Builder.Default
    @Column(name = "tier_level")
    private String tierLevel = "BRONZE";

    @Column(name = "personalized_recommendations")
    private String personalizedRecommendations;

    @Column(name = "privacy_consent")
    private Boolean privacyConsent;

    @Column(name = "data_usage_preferences")
    private String dataUsagePreferences;

    public LoyaltyProgram(String patientId, String programName) {
        this.patientId = patientId;
        this.programName = programName;
        this.enrollmentDate = LocalDateTime.now();
    }
}
