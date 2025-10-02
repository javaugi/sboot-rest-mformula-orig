/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.wmart.validrule;

import com.spring5.dbisolation.wmart.FraudService;
import com.spring5.kafkamicroservice.Payment;
import lombok.AllArgsConstructor;
import static org.eclipse.jdt.internal.compiler.ReadManager.THRESHOLD;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class FraudScoreValidationRule implements ValidationRule {

	private final FraudService fraudService;

	@Override
	@Cacheable(value = "fraudScores", key = "#payment.getUserId()")
	public boolean validate(Payment payment) {
		// This method's result is now cached based on the user ID
		return fraudService.getScore(payment.getUserId()) < THRESHOLD;
	}

	@Override
	public String getRuleName() {
		return "RULE1";
	}

}
