/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.utils.common;

import com.spring5.aicloud.genaihealthcare.cccr.*;
import reactor.core.publisher.Mono;

/**
 * @author javau
 */
public class ReactiveClinicalRulesEngine {

    public ValidationResult validate(ReactiveClaimEvent claim) {
        return ValidationResult.builder().build();
    }

    public boolean isCrossClaimEligible(ReactiveClaimEvent claim) {
        return true;
    }

    public Mono<ReactiveReviewResult> applyClinicalRules(ReactiveClaimEvent claim) {
        return Mono.just(ReactiveReviewResult.builder().build());
    }
}
