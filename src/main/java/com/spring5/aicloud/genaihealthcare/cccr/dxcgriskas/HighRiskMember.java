/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.aicloud.genaihealthcare.cccr.dxcgriskas;

import java.util.List;

@lombok.Data
@lombok.Builder(toBuilder = true)
public class HighRiskMember {

	private String memberId;

	private double riskScore;

	private double predictedCost;

	private List<String> topConditions;

}
