/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.aicloud.genaihealthcare.cccr.dxcgriskas;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import static org.apache.commons.math3.util.Precision.round;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("!production")
@Slf4j
public class DxcgStubClient {

	// implements DxcgClient {

	private static final Map<String, Double> MODEL_BASELINES = Map.of("CMS-HCC", 1.0, "HHS-HCC", 1.0, "RX-HCC", 1.0);

	// @Override
	public RiskScoreResponse calculateRiskScore(RiskCalculationRequest request) {
		log.info("Stub implementation - simulating DxCG risk calculation");

		// Simulate API delay
		simulateProcessingDelay();

		double baseScore = calculateStubRiskScore(request);
		double predictedCost = baseScore * 10000; // Simplified cost model

		return RiskScoreResponse.builder()
			.memberId(request.getMemberId())
			.modelType(request.getModelType())
			.riskScore(round(baseScore, 3))
			.predictedCost(round(predictedCost, 2))
			.calculationDate(LocalDate.now())
			.modelVersion("V24.0.STUB")
			.hccContributions(calculateStubHccContributions(request))
			.build();
	}

	private List<HccContribution> calculateStubHccContributions(RiskCalculationRequest request) {
		return Collections.EMPTY_LIST;
	}

	private void simulateProcessingDelay() {
	}

	private double calculateStubRiskScore(RiskCalculationRequest request) {
		double baseScore = MODEL_BASELINES.getOrDefault(request.getModelType(), 1.0);

		// Add contributions from diagnoses
		double diagnosisContribution = request.getDiagnoses()
			.stream()
			.mapToDouble(d -> getStubHccWeight(d.getIcd10Code()))
			.sum();

		// Add demographic factors
		double demographicFactor = calculateDemographicFactor(request.getDemographics());

		return baseScore + diagnosisContribution * demographicFactor;
	}

	private double calculateDemographicFactor(RiskCalculationRequest.MemberDemographics demographics) {
		return 10D;
	}

	private double getStubHccWeight(String icd10Code) {
		// Simplified HCC weighting (real implementation would use actual DxCG mappings)
		Map<String, Double> stubWeights = Map.of("I25", 0.543, "E11", 0.321, "I50", 0.234, "J44", 0.456, "N18", 0.289,
				"F32", 0.387);

		String rootCode = icd10Code.length() >= 3 ? icd10Code.substring(0, 3) : icd10Code;
		return stubWeights.getOrDefault(rootCode, 0.1);
	}

}
