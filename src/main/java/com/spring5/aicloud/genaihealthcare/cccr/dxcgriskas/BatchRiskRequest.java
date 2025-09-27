/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.aicloud.genaihealthcare.cccr.dxcgriskas;

import java.util.List;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.jackson.Jacksonized;

@lombok.Data
@lombok.Builder(toBuilder = true)
@Jacksonized
public class BatchRiskRequest {
    @NotEmpty
    private List<String> memberIds;

    @NotBlank
    private String modelType;

    private String batchId;
    private List<RiskCalculationRequest> requests;
}
