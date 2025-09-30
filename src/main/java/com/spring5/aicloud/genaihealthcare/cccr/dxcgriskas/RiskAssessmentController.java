/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.aicloud.genaihealthcare.cccr.dxcgriskas;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/risk-assessment")
@Validated
public class RiskAssessmentController {

    private final DxcgRiskAssessmentService riskService;

    public RiskAssessmentController(DxcgRiskAssessmentService riskService) {
        this.riskService = riskService;
    }

    @PostMapping("/calculate")
    public ResponseEntity<RiskScore> calculateRiskScore(
            @RequestBody @Valid RiskCalculationRequest request) {

        RiskScore riskScore
                = riskService.calculateRiskScore(request.getMemberId(), request.getModelType());

        return ResponseEntity.ok(riskScore);
    }

    @PostMapping("/calculate-batch")
    public ResponseEntity<BatchRiskResponse> calculateBatchRiskScores(
            @RequestBody @Valid BatchRiskRequest request) {

        CompletableFuture<List<RiskScore>> future
                = riskService.calculateBatchRiskScores(request.getMemberIds(), request.getModelType());

        BatchRiskResponse response
                = BatchRiskResponse.builder()
                        .batchId(UUID.randomUUID().toString())
                        .status("PROCESSING")
                        .message("Risk assessment started for " + request.getMemberIds().size() + " members")
                        .build();

        return ResponseEntity.accepted().body(response);
    }

    @GetMapping("/members/{memberId}/scores")
    public ResponseEntity<List<RiskScore>> getMemberRiskScores(
            @PathVariable String memberId, @RequestParam(required = false) String modelType) {

        List<RiskScore> scores = riskService.getRiskScoresByMember(memberId, modelType);
        return ResponseEntity.ok(scores);
    }

    @GetMapping("/population-analysis")
    public ResponseEntity<PopulationRiskAnalysis> analyzePopulationRisk(
            @RequestParam String planType, @RequestParam(required = false) Integer year) {

        PopulationRiskAnalysis analysis = riskService.analyzePopulationRisk(planType, year);
        return ResponseEntity.ok(analysis);
    }
}
