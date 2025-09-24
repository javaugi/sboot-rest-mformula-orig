/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.aicloud.genaihealthcare.cccr;

/**
 *
 * @author javau
 */
public class ClinicalRulesEngine {

    public ValidationResult validate(Claim claim) {
        return ValidationResult.builder().build();
    }

    public boolean isCrossClaimEligible(Claim claim) {
        return true;
    }

    public ReviewResult applyRules(Claim claim) {
        return ReviewResult.builder().build();
    }
}
