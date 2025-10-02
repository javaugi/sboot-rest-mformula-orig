/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.aicloud.genaihealthcare.cccr;

import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class ValidationResult {

	double estimatedSavings;

	boolean valid;

	@Builder.Default
	List<ValidationIssue> issues = new ArrayList<>();

	@Builder.Default
	List<ReviewOpportunity> opprs = new ArrayList<>();

	public void addIssue(ValidationIssue issue) {
		this.issues.add(issue);
	}

	public void addOpportunity(ReviewOpportunity oppr) {
		opprs.add(oppr);
	}

}
