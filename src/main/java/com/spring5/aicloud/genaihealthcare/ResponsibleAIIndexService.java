/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.aicloud.genaihealthcare;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ResponsibleAIIndexService {

    private final ResponsibleAIIndexRepository repository;

    public List<ResponsibleAIIndex> getAllCompliantModels() {
        return repository.findByComplianceStatus("COMPLIANT");
    }

    public ResponsibleAIIndex saveOrUpdateIndex(ResponsibleAIIndex index) {
        return repository.save(index);
    }

    public List<ResponsibleAIIndex> getModelsByMinScore(Double minScore) {
        return repository.findByMinOverallScore(minScore);
    }

    public ResponsibleAIIndex getModelByName(String modelName) {
        return repository.findByModelName(modelName);
    }

    public Double getIndustryAverageScore() {
        List<ResponsibleAIIndex> allModels = repository.findAll();
        return allModels.stream()
            .mapToDouble(ResponsibleAIIndex::getOverallScore)
            .average()
            .orElse(0.0);
    }

    public ResponsibleAIIndex computeAndStore(String operation, String modelName, String notes, double transparency, double fairness, double privacy, double regulatoryAlignment) {
        ResponsibleAIIndex r = new ResponsibleAIIndex();
        r.operation = operation;
        r.modelName = modelName;
        r.transparencyScore = clamp(transparency);
        r.fairnessScore = clamp(fairness);
        r.privacyScore = clamp(privacy);
        r.regulatoryAlignmentScore = clamp(regulatoryAlignment);
        r.notes = notes;
        return repository.save(r);
    }

    private double clamp(double v) {
        if (Double.isNaN(v) || Double.isInfinite(v)) {
            return 0.0;
        }
        if (v < 0) {
            return 0;
        }
        if (v > 1) {
            return 1;
        }
        return v;
    }

    /**
     * Example helper: derive transparency heuristics
     */
    public Map<String, Double> deriveScoresFromResponse(String prompt, String response, double modelConfidence, boolean consentPresent) {
        Map<String, Double> scores = new HashMap<>();
        // Simple heuristics — replace with real evaluators and policy rules
        double transparency = 0.6 + (response != null ? 0.2 : 0.0);
        double fairness = 0.7; // requires offline fairness eval
        double privacy = consentPresent ? 0.9 : 0.2;
        double regulatory = 0.8; // e.g., if using only approved models
        scores.put("transparency", clamp(transparency));
        scores.put("fairness", clamp(fairness));
        scores.put("privacy", clamp(privacy));
        scores.put("regulatory", clamp(regulatory));
        return scores;
    }
}
