/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.aicloud.genaihealthcare.cccr.dxcgriskas;

import com.fasterxml.jackson.databind.ObjectMapper;
//import static jakarta.persistence.GenerationType.UUID;
//import static java.lang.Math.log;
import java.time.Duration;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.asm.AsmManager.ModelInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class VeriskDxcgClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${verisk.dxcg.api.base-url}")
    private String baseUrl;

    @Value("${verisk.dxcg.api.key}")
    private String apiKey;

    @Value("${verisk.dxcg.api.secret}")
    private String apiSecret;

    public VeriskDxcgClient(RestTemplateBuilder restTemplateBuilder,
        ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.restTemplate = restTemplateBuilder
            .rootUri(baseUrl)
            .additionalInterceptors(new AuthInterceptor())
            .connectTimeout(Duration.ofSeconds(30))
            .readTimeout(Duration.ofSeconds(60))
            .build();
    }

    /**
     * Submit claims data for risk scoring
     */
    public RiskScoreResponse submitRiskCalculation(RiskCalculationRequest request) {
        try {
            ResponseEntity<RiskScoreResponse> response = restTemplate.postForEntity(
                "/risk/calculate",
                request,
                RiskScoreResponse.class
            );

            return response.getBody();
        } catch (HttpClientErrorException e) {
            log.error("DxCG API error: {}", e.getResponseBodyAsString());
            throw new DxcgIntegrationException("Failed to calculate risk score", e);
        }
    }

    /**
     * Batch risk score calculation
     */
    public BatchRiskResponse submitBatchRiskCalculation(List<RiskCalculationRequest> requests) {
        BatchRiskRequest batchRequest = BatchRiskRequest.builder()
            .requests(requests)
            .batchId(java.util.UUID.randomUUID().toString())
            .build();

        try {
            ResponseEntity<BatchRiskResponse> response = restTemplate.postForEntity(
                "/risk/batch/calculate",
                batchRequest,
                BatchRiskResponse.class
            );

            return response.getBody();
        } catch (Exception e) {
            throw new DxcgIntegrationException("Batch risk calculation failed", e);
        }
    }

    /**
     * Get model information and version
     */
    public ModelInfo getModelInfo(String modelType) {
        try {
            return restTemplate.getForObject("/models/{modelType}", ModelInfo.class, modelType);
        } catch (Exception e) {
            throw new DxcgIntegrationException("Failed to get model info", e);
        }
    }

}
