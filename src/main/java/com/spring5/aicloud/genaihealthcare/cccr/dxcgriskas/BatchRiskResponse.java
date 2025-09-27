/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.aicloud.genaihealthcare.cccr.dxcgriskas;

import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
@lombok.Data
@lombok.Builder(toBuilder = true)
@Jacksonized
public class BatchRiskResponse {
    private String batchId;
    private String status;
    private String message;

    private List<RiskScoreResponse> results;
    private List<ProcessingError> errors;

}
