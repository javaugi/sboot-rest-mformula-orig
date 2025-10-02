/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.aicloud.genaihealthcare.cccr.dxcgriskas;

@lombok.Data
@lombok.Builder(toBuilder = true)
public class RiskValidationMetrics {

	private Double rSquared;

	private Double meanAbsoluteError;

	private Double predictiveRatio;

}
