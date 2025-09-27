/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.aicloud.genaihealthcare.cccr.dxcgriskas;

import com.spring5.aicloud.genaihealthcare.cccr.Claim;
import com.spring5.entity.Patient;
import com.spring5.repository.PatientRepository;
import java.time.LocalDate;
import java.time.Period;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
/*
8. Key Use Cases Implemented
    Individual Risk Scoring: Calculate CMS-HCC, HHS-HCC, or commercial risk scores
    Population Risk Analysis: Analyze entire member populations for RAF reporting
    High-Risk Identification: Flag members for care management programs
    Risk Validation: Validate risk scores against actual costs
    Batch Processing: Handle large member populations efficiently
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DxcgRiskAssessmentService {

    private final PatientRepository patientRepository;
    private final RiskScoreRepository riskScoreRepository;
    private final DxcgApiClient dxcgApiClient;

    // DxCG model factors (simplified - real implementation would use actual DxCG algorithms)
    private static final Map<String, Double> HCC_WEIGHTS = Map.of(
        "HCC1", 0.543, "HCC2", 0.321, "HCC6", 0.234, "HCC8", 0.456,
        "HCC10", 0.289, "HCC17", 0.387, "HCC18", 0.412
    );

    /*
    public DxcgRiskAssessmentService(PatientRepository patientRepository,
        RiskScoreRepository riskScoreRepository,
        DxcgApiClient dxcgApiClient) {
        this.patientRepository = patientRepository;
        this.riskScoreRepository = riskScoreRepository;
        this.dxcgApiClient = dxcgApiClient;
    }
    // */
    public PopulationRiskAnalysis analyzePopulationRisk(String planType, Integer year) {
        return PopulationRiskAnalysis.builder().build();
    }

    public List<RiskScore> getRiskScoresByMember(String memberId, String modelType) {
        return Collections.EMPTY_LIST;
    }

    /**
     * Calculate risk score for a single patient using DxCG methodology
     */
    public RiskScore calculateRiskScore(String id, String modelType) {
        Patient patient = patientRepository.findById(Long.valueOf(id))
            .orElse(Patient.builder().id(Long.valueOf(id)).build());

        // Extract HCCs from patient claims
        Set<String> hccCodes = extractHccCodes(patient);

        // Calculate base risk score
        Double baseScore = calculateBaseRiskScore(hccCodes, modelType);

        // Apply demographic factors
        Double demographicFactor = calculateDemographicFactor(patient);

        // Apply interaction factors
        Double interactionFactor = calculateInteractionFactor(hccCodes);

        Double finalRiskScore = baseScore * demographicFactor * interactionFactor;
        Double predictedCost = calculatePredictedCost(finalRiskScore, modelType);

        RiskScore riskScore = RiskScore.builder()
            .patient(patient)
            .calculationDate(LocalDate.now())
            .modelType(modelType)
            .riskScore(finalRiskScore)
            .predictedCost(predictedCost)
            .hierarchicalConditionCategories(mapHccToDescriptions(hccCodes))
            .build();

        return riskScoreRepository.save(riskScore);
    }

    private Map<String, String> mapHccToDescriptions(Set<String> codes) {
        return new HashMap<>();
    }

    /**
     * Batch risk assessment for multiple members
     */
    @Async
    public CompletableFuture<List<RiskScore>> calculateBatchRiskScores(
        List<String> memberIds, String modelType) {

        List<RiskScore> results = memberIds.parallelStream()
            .map(memberId -> {
                try {
                    return calculateRiskScore(memberId, modelType);
                } catch (Exception e) {
                    log.error("Failed to calculate risk for member {}: {}", memberId, e.getMessage());
                    return null;
                }
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

        return CompletableFuture.completedFuture(results);
    }

    /**
     * Extract HCC codes from patient claims using DxCG grouper logic
     */
    private Set<String> extractHccCodes(Patient patient) {
        return patient.getClaims().stream()
            .map(Claim::getDiagnosisCode)
            .filter(Objects::nonNull)
            .map(this::mapIcd10ToHcc)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
    }

    /**
     * Map ICD-10 codes to HCC categories (simplified mapping)
     */
    private String mapIcd10ToHcc(String icd10Code) {
        // Simplified mapping - real implementation would use DxCG grouper
        Map<String, String> icd10ToHcc = Map.of(
            "I25", "HCC1", // Atherosclerotic heart disease
            "E11", "HCC2", // Diabetes mellitus
            "I50", "HCC6", // Heart failure
            "J44", "HCC8", // COPD
            "N18", "HCC10", // Chronic kidney disease
            "F32", "HCC17", // Major depression
            "G30", "HCC18" // Alzheimer's disease
        );

        String rootCode = icd10Code.substring(0, 3);
        return icd10ToHcc.get(rootCode);
    }

    private Double calculateBaseRiskScore(Set<String> hccCodes, String modelType) {
        return hccCodes.stream()
            .mapToDouble(hcc -> HCC_WEIGHTS.getOrDefault(hcc, 0.0))
            .sum();
    }

    private Double calculateDemographicFactor(Patient patient) {
        int age = Period.between(patient.getDateOfBirth(), LocalDate.now()).getYears();
        double ageFactor = calculateAgeFactor(age);
        double genderFactor = "M".equals(patient.getGender()) ? 1.1 : 1.0;

        return ageFactor * genderFactor;
    }

    private Double calculateAgeFactor(int age) {
        if (age < 35) {
            return 0.8;
        }
        if (age < 45) {
            return 0.9;
        }
        if (age < 55) {
            return 1.0;
        }
        if (age < 65) {
            return 1.2;
        }
        if (age < 75) {
            return 1.5;
        }
        return 1.8; // 75+
    }

    private Double calculateInteractionFactor(Set<String> hccCodes) {
        // Calculate interactions between conditions
        double interaction = 1.0;

        if (hccCodes.contains("HCC1") && hccCodes.contains("HCC2")) {
            interaction *= 1.15; // Heart disease + diabetes interaction
        }
        if (hccCodes.contains("HCC6") && hccCodes.contains("HCC8")) {
            interaction *= 1.20; // Heart failure + COPD interaction
        }

        return interaction;
    }

    private Double calculatePredictedCost(Double riskScore, String modelType) {
        double baseCost;
        switch (modelType) {
            case "MEDICARE":
                baseCost = 12000.0; // Average Medicare cost
                break;
            case "COMMERCIAL":
                baseCost = 8000.0; // Average commercial cost
                break;
            case "ACA":
                baseCost = 10000.0; // Average ACA cost
                break;
            default:
                baseCost = 10000.0;
        }
        return riskScore * baseCost;
    }

}
