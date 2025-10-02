/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.wmart;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CurrencyConverter {

	// return value Map<transId, Amount> from all the transactions and the exchange rates
	public Map<String, Double> convertTransactions(List<String> transactions, List<String> exchangeRates) {
		// Step 1: Parse exchange rates into a map for efficient lookup.
		// Key: Currency (e.g., "EUR"), Value: Conversion factor to USD.
		Map<String, Double> ratesMap = getRateMap(exchangeRates);

		// Step 2: Process each transaction and convert the amount.
		Map<String, Double> convertedAmounts = new HashMap<>();
		for (String transactionStr : transactions) {
			String[] parts = transactionStr.split(",");
			// 1. Id of the transaction, 2 The amount, 3, The currency
			if (parts.length == 3) {
				String id = parts[0].trim();
				double amount = Double.parseDouble(parts[1].trim());
				String currency = parts[2].trim();

				// Look up the exchange rate. If not found, skip or handle as an error.
				Double rate = ratesMap.get(currency);
				if (rate != null) {
					double convertedAmount = amount * rate;
					convertedAmounts.put(id, convertedAmount);
				}
				else {
					// Log or handle the case of a missing exchange rate
					// In real world we'll probably thrown an No Rate Exception
					System.err.println("Warning: Missing exchange rate for currency " + currency);
				}
			}
		}
		return convertedAmounts;
	}

	private Map<String, Double> getRateMap(List<String> exchangeRates) throws NumberFormatException {
		// Step 1: Parse exchange rates into a map for efficient lookup.
		// Key: Currency (e.g., "EUR"), Value: Conversion factor to USD.
		Map<String, Double> ratesMap = new HashMap<>();

		for (String rateStr : exchangeRates) {
			// 1. FromCurrency, 2 ToCurrency, 3, Rate
			String[] parts = rateStr.split(",");
			if (parts.length == 3) {
				String fromCurrency = parts[0].trim();
				String toCurrency = parts[1].trim();
				double rate = Double.parseDouble(parts[2].trim());
				// Assuming all rates are to USD, we can store them directly.
				if ("USD".equalsIgnoreCase(toCurrency)) {
					ratesMap.put(fromCurrency, rate);
				}
			}
		}
		// Add a base rate for the target currency itself
		ratesMap.put("USD", 1.0);

		return ratesMap;
	}

}
