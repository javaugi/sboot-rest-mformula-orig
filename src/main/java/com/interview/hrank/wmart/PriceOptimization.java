/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.interview.hrank.wmart;

/**
 * Problem: Dynamic pricing optimization based on demand, competition, and inventory
 * levels Similar to Walmart's pricing strategy system
 */
public class PriceOptimization {

	public static double maxRevenue(int[] inventory, double[] basePrices, double[] demandCurve,
			double[] competitorPrices) {
		int n = inventory.length;
		double maxRevenue = 0.0;

		// Try different pricing strategies
		for (int i = 0; i < n; i++) {
			double optimalPrice = findOptimalPrice(basePrices[i], demandCurve[i], competitorPrices[i]);
			double expectedDemand = calculateDemand(optimalPrice, basePrices[i], demandCurve[i], competitorPrices[i]);
			double revenue = optimalPrice * Math.min(expectedDemand, inventory[i]);
			maxRevenue += revenue;
		}

		return maxRevenue;
	}

	private static double findOptimalPrice(double basePrice, double elasticity, double competitorPrice) {
		// Simple pricing strategy: balance between max profit and competition
		double targetPrice = (basePrice + competitorPrice) / 2.0;
		double elasticityFactor = 1.0 / (1.0 + Math.abs(elasticity));

		return basePrice * (1.0 - elasticityFactor) + targetPrice * elasticityFactor;
	}

	private static double calculateDemand(double price, double basePrice, double elasticity, double competitorPrice) {
		double priceRatio = price / basePrice;
		double compRatio = price / competitorPrice;

		// Demand decreases as price increases relative to base and competitor prices
		double demand = 100.0; // base demand
		demand *= Math.exp(-elasticity * (priceRatio - 1.0));
		demand *= Math.exp(-0.5 * (compRatio - 1.0));

		return Math.max(demand, 0.0);
	}

	// Test cases
	public static void main(String[] args) {
		// Test Case 1: Electronics department
		int[] inventory1 = { 50, 30, 100 }; // TVs, Laptops, Headphones
		double[] basePrices1 = { 499.99, 899.99, 79.99 };
		double[] elasticity1 = { 0.2, 0.3, 0.1 }; // price elasticity
		double[] compPrices1 = { 479.99, 879.99, 69.99 }; // competitor prices

		double revenue1 = maxRevenue(inventory1, basePrices1, elasticity1, compPrices1);
		System.out.printf("Test 1 Max Revenue: $%.2f%n", revenue1);

		// Test Case 2: Grocery items
		int[] inventory2 = { 200, 150, 300 }; // Milk, Bread, Eggs
		double[] basePrices2 = { 3.99, 2.49, 4.99 };
		double[] elasticity2 = { 0.05, 0.08, 0.03 }; // lower elasticity for essentials
		double[] compPrices2 = { 3.79, 2.29, 4.79 };

		double revenue2 = maxRevenue(inventory2, basePrices2, elasticity2, compPrices2);
		System.out.printf("Test 2 Max Revenue: $%.2f%n", revenue2);
	}

}
