/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.interview.hrank.wmart;

import java.util.Arrays;

/**
 * Problem: Predict inventory needs based on historical sales data and trends Similar to
 * Walmart's demand forecasting system
 */
public class InventoryForecasting {

	public static int[] forecastInventory(int[] historicalSales, int weeksToForecast, String seasonality) {
		int[] forecast = new int[weeksToForecast];

		// Simple moving average with seasonality factor
		double baseDemand = calculateBaseDemand(historicalSales);
		double seasonalityFactor = getSeasonalityFactor(seasonality);
		double baseweeklyDemand = baseDemand * seasonalityFactor;

		for (int week = 0; week < weeksToForecast; week++) {
			double weeklyDemand = baseweeklyDemand;
			// Add some random variation (real system would use more sophisticated model)
			weeklyDemand *= (0.9 + 0.3 * Math.random());
			forecast[week] = (int) Math.max(weeklyDemand, historicalSales[0] * 0.5);

			System.out.println("forecastInventory    baseDemand=" + baseDemand + "-baseweeklyDemand=" + baseweeklyDemand
					+ "-weeklyDemand=" + weeklyDemand + "\n-historicalSales[0]=" + historicalSales[0]
					+ "-forecast[week]=" + forecast[week]);
		}

		return forecast;
	}

	public static int[] forecastInventoryMy(int[] historicalSales, int weeksToForecast, String seasonality) {
		int[] forecast = new int[weeksToForecast];

		// Simple moving average with seasonality factor
		double baseDemand = calculateBaseDemand(historicalSales);
		double averageDemand = calculateBaseAverage(historicalSales);
		double seasonalityFactor = getSeasonalityFactor(seasonality);
		double baseweeklyDemand = baseDemand * seasonalityFactor;

		for (int week = 0; week < weeksToForecast; week++) {
			double weeklyDemand = baseweeklyDemand;
			// Add some random variation (real system would use more sophisticated model)
			weeklyDemand *= (0.9 + 0.3 * Math.random());
			forecast[week] = (int) Math.max(weeklyDemand, historicalSales[0] * 0.5);

			System.out.println("forecastInventory    baseDemand=" + baseDemand + "-baseweeklyDemand=" + baseweeklyDemand
					+ "-weeklyDemand=" + weeklyDemand + "\n-historicalSales[0]=" + historicalSales[0]
					+ "-forecast[week]=" + forecast[week]);
		}

		return forecast;
	}

	public static int[] forecastInventorySimplified(int[] historicalSales, int weeksToForecast, String seasonality) {
		int[] forecast = new int[weeksToForecast];

		// Simple moving average
		double averageDemand = calculateBaseAverage(historicalSales);
		double seasonalityFactor = getSeasonalityFactor(seasonality);
		double baseweeklyDemand = averageDemand * seasonalityFactor;

		for (int week = 0; week < weeksToForecast; week++) {
			double weeklyDemand = baseweeklyDemand;
			// Add some random variation (real system would use more sophisticated model)
			weeklyDemand *= (0.9 + 0.3 * Math.random());
			forecast[week] = (int) Math.max(weeklyDemand, historicalSales[0] * 0.5);

			System.out.println("forecastInventory    averageDemand=" + averageDemand + "-baseweeklyDemand="
					+ baseweeklyDemand + "-weeklyDemand=" + weeklyDemand + "\n-historicalSales[0]=" + historicalSales[0]
					+ "-forecast[week]=" + forecast[week]);
		}

		return forecast;
	}

	private static double calculateBaseAverage(int[] historicalSales) {
		return Arrays.stream(historicalSales).sum() / historicalSales.length;
	}

	private static double calculateBaseDemand(int[] historicalSales) {
		if (historicalSales.length == 0) {
			return 0;
		}

		// Weighted average: recent weeks matter more
		double total = 0;
		double weight = 1.0;
		double totalWeight = 0;

		for (int i = historicalSales.length - 1; i >= 0; i--) {
			total += historicalSales[i] * weight;
			totalWeight += weight;
			weight *= 0.9; // Decrease weight for older data
		}

		return total / totalWeight;
	}

	public static int[] forecastInventorySimplified2(int[] historicalSales, int weeksToForecast, String seasonality) {
		System.out.println("****** forecastInventorySimplified2 ...");
		int[] forecast = new int[weeksToForecast];

		// Simple moving average
		double baseDemand = calculateBaseDemandSimplified(historicalSales);
		double seasonalityFactor = getSeasonalityFactor(seasonality);
		double baseweeklyDemand = baseDemand * seasonalityFactor;

		for (int week = 0; week < weeksToForecast; week++) {
			double weeklyDemand = baseweeklyDemand;
			// Add some random variation (real system would use more sophisticated model)
			weeklyDemand *= (0.9 + 0.3 * Math.random());
			forecast[week] = (int) Math.max(weeklyDemand, historicalSales[0] * 0.5);

			System.out.println("forecastInventory    baseDemand=" + baseDemand + "-baseweeklyDemand=" + baseweeklyDemand
					+ "-weeklyDemand=" + weeklyDemand + "\n-historicalSales[0]=" + historicalSales[0]
					+ "-forecast[week]=" + forecast[week]);
		}

		return forecast;
	}

	private static double calculateBaseDemandSimplified(int[] historicalSales) {
		if (historicalSales.length == 0) {
			return 0;
		}

		// Weighted average: recent weeks matter more
		double total = 0;
		double weight = 1.0;
		double totalWeight = 0;

		for (int i = 0; i < historicalSales.length; i++) {
			total += historicalSales[i] * weight;
			totalWeight += weight;
			weight *= 1.2; // Increate weight for new data
		}

		return total / totalWeight;
	}

	private static double getSeasonalityFactor(String seasonality) {
		switch (seasonality.toLowerCase()) {
			case "holiday":
				return 1.5;
			case "summer":
				return 1.2;
			case "winter":
				return 1.1;
			case "spring":
				return 0.9;
			case "fall":
				return 0.95;
			default:
				return 1.0;
		}
	}

	// Test cases
	public static void main(String[] args) {
		// Test Case 1: Regular product
		int[] sales1 = { 100, 110, 105, 115, 120, 125, 130, 135 }; // 8 weeks of sales

		System.out.println("Test 1 - Next 4 weeks forecast:");
		int[] forecast1 = forecastInventory(sales1, 4, "regular");
		for (int i = 0; i < forecast1.length; i++) {
			System.out.println("Week " + (i + 1) + ": " + forecast1[i] + " units");
		}

		// Test Case 2: Holiday season
		int[] sales2 = { 200, 180, 190, 210, 220, 230 };

		System.out.println("\nTest 2 (forecastInventory) - Holiday season forecast:");
		int[] forecast2 = forecastInventory(sales2, 3, "holiday");
		for (int i = 0; i < forecast2.length; i++) {
			System.out.println("Week " + (i + 1) + ": " + forecast2[i] + " units");
		}

		// Test Case 3: Holiday season
		System.out.println("\nTest 3 (forecastInventoryMy) - Holiday season forecast:");
		int[] forecast3 = forecastInventoryMy(sales2, 3, "holiday");
		for (int i = 0; i < forecast3.length; i++) {
			System.out.println("Week " + (i + 1) + ": " + forecast3[i] + " units");
		}

		// Test Case 4: Holiday season
		System.out.println("\nTest 4 (forecastInventorySimplified) - Holiday season forecast:");
		int[] forecast4 = forecastInventorySimplified(sales2, 3, "holiday");
		for (int i = 0; i < forecast4.length; i++) {
			System.out.println("Week " + (i + 1) + ": " + forecast4[i] + " units");
		}

		// Test Case 5: Holiday season
		System.out.println("\nTest 5 (forecastInventorySimplified2) - Holiday season forecast:");
		int[] forecast5 = forecastInventorySimplified2(sales2, 3, "holiday");
		for (int i = 0; i < forecast5.length; i++) {
			System.out.println("Week " + (i + 1) + ": " + forecast5[i] + " units");
		}

	}

}
