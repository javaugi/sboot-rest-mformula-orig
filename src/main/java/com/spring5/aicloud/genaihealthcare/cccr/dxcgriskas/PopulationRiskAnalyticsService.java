/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.aicloud.genaihealthcare.cccr.dxcgriskas;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class PopulationRiskAnalyticsService {

    private final RiskScoreRepository riskScoreRepository;

    public PopulationRiskAnalyticsService(RiskScoreRepository riskScoreRepository) {
        this.riskScoreRepository = riskScoreRepository;
    }

    /**
     * Analyze population risk for risk adjustment factor (RAF) reporting
     */
    public PopulationRiskAnalysis analyzePopulationRisk(String planType, Integer year) {
        List<RiskScore> scores = riskScoreRepository.findByPlanTypeAndYear(planType, year);

        double averageRiskScore = calculateAverageRiskScore(scores);
        double totalPredictedCost = calculateTotalPredictedCost(scores);
        Map<String, Double> hccPrevalence = calculateHccPrevalence(scores);
        RiskDistribution distribution = calculateRiskDistribution(scores);

        return PopulationRiskAnalysis.builder()
                .planType(planType)
                .year(year != null ? year : LocalDate.now().getYear())
                .totalMembers(scores.size())
                .averageRiskScore(averageRiskScore)
                .totalPredictedCost(totalPredictedCost)
                .hccPrevalence(hccPrevalence)
                .riskDistribution(distribution)
                .build();
    }

    private double calculateAverageRiskScore(List<RiskScore> scores) {
        return 10D;
    }

    private double calculateTotalPredictedCost(List<RiskScore> scores) {
        return 10D;
    }

    private Map<String, Double> calculateHccPrevalence(List<RiskScore> scores) {
        return Map.of();
    }

    private RiskDistribution calculateRiskDistribution(List<RiskScore> scores) {
        return RiskDistribution.builder().build();
    }

    /**
     * Identify high-risk members for care management programs
     */
    public List<HighRiskMember> identifyHighRiskMembers(double riskThreshold) {
        return riskScoreRepository.findLatestScores().stream()
                .filter(score -> score.getRiskScore() > riskThreshold)
                .map(
                        score
                        -> HighRiskMember.builder()
                                .memberId(score.getPatient().getMemberId())
                                .riskScore(score.getRiskScore())
                                .predictedCost(score.getPredictedCost())
                                .topConditions(extractTopConditions(score))
                                .build())
                .sorted(Comparator.comparing(HighRiskMember::getRiskScore).reversed())
                .collect(Collectors.toList());
    }

    private List<String> extractTopConditions(RiskScore score) {
        return Collections.emptyList();
    }

    /**
     * Calculate risk adjustment validation metrics
     */
    public RiskValidationMetrics calculateValidationMetrics() {
        // This would compare predicted vs actual costs
        // Used for CMS RAF validation and audit preparation
        return RiskValidationMetrics.builder()
                .rSquared(0.85)
                .meanAbsoluteError(1500.0)
                .predictiveRatio(1.02)
                .build();
    }
}
