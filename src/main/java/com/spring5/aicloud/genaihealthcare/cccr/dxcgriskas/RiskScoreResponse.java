/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.aicloud.genaihealthcare.cccr.dxcgriskas;

import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
public class RiskScoreResponse {

    private String memberId;
    private String modelType;
    private Double riskScore;
    private Double predictedCost;
    private LocalDate calculationDate;
    private String modelVersion;

    private List<HccContribution> hccContributions;
    private List<InteractionFactor> interactionFactors;
    private DemographicFactor demographicFactors;

}
