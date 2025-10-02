/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.wmart.validrule;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FraudDetector {

	// Store the list of all available fraud rules
	private final List<FraudRule> allRules;

	public FraudDetector() {
		// Initialize with all the fraud rules. This can be done via dependency injection.
		this.allRules = new ArrayList<>();
		this.allRules.add(new LargeTransactionRule());
		this.allRules.add(new GeoVelocityRule());
	}

	public List<Transaction> detectSuspiciousActivity(List<Transaction> transactions, FraudDetectionConfig config) {
		List<Transaction> suspiciousTransactions = new ArrayList<>();

		for (Transaction transaction : transactions) {
			boolean isSuspicious = false;
			// Iterate through each rule and check if the transaction is suspicious
			for (FraudRule rule : allRules) {
				if (rule.isSuspicious(transaction, config)) {
					isSuspicious = true;
					// We can add a more detailed flag to the transaction itself
					// to indicate which rules it failed.
					break;
				}
			}
			if (isSuspicious) {
				suspiciousTransactions.add(transaction);
			}
		}
		return suspiciousTransactions;
	}

	public List<Transaction> detectSuspiciousActivityParallel(List<Transaction> transactions,
			FraudDetectionConfig config) {
		return transactions.parallelStream()
			.filter(transaction -> allRules.stream().anyMatch(rule -> rule.isSuspicious(transaction, config)))
			.collect(Collectors.toList());
	}

}
