/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.aicloud.genaihealthcare.cccr.dxcgriskas;

import java.util.Map;

@lombok.Data
@lombok.Builder(toBuilder = true)
public class PopulationRiskAnalysis {

    private String planType;
    private int year;
    private int totalMembers;
    private double averageRiskScore;
    private double totalPredictedCost;
    private Map<String, Double> hccPrevalence;
    private RiskDistribution riskDistribution;
}
