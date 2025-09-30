/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.interview.hrank.wmart;

import java.util.Arrays;

/**
 * Problem: Predict inventory needs based on historical sales data and trends
 * Similar to Walmart's demand forecasting system
 */
public class InventoryForecasting {

    public static int[] forecastInventory(
            int[] historicalSales, int weeksToForecast, String seasonality) {
        int[] forecast = new int[weeksToForecast];

        // Simple moving average with seasonality factor
        double baseDemand = calculateBaseDemand(historicalSales);
        double seasonalityFactor = getSeasonalityFactor(seasonality);

        for (int week = 0; week < weeksToForecast; week++) {
            double weeklyDemand = baseDemand * seasonalityFactor;
            // Add some random variation (real system would use more sophisticated model)
            weeklyDemand *= (0.9 + 0.2 * Math.random());
            forecast[week] = (int) Math.max(weeklyDemand, historicalSales[0] * 0.5);
        }

        return forecast;
    }

    public static int[] forecastInventoryMy(
            int[] historicalSales, int weeksToForecast, String seasonality) {
        int[] forecast = new int[weeksToForecast];

        // Simple moving average with seasonality factor
        double baseDemand = calculateBaseDemand(historicalSales);
        double averageDemand = calculateBaseAverage(historicalSales);
        double seasonalityFactor = getSeasonalityFactor(seasonality);

        for (int week = 0; week < weeksToForecast; week++) {
            double weeklyDemand = baseDemand * seasonalityFactor;
            // Add some random variation (real system would use more sophisticated model)
            weeklyDemand *= (0.9 + 0.2 * Math.random());
            forecast[week] = (int) Math.max(weeklyDemand, averageDemand);
        }

        return forecast;
    }

    public static int[] forecastInventorySimplified(
            int[] historicalSales, int weeksToForecast, String seasonality) {
        int[] forecast = new int[weeksToForecast];

        // Simple moving average
        double averageDemand = calculateBaseAverage(historicalSales);
        double seasonalityFactor = getSeasonalityFactor(seasonality);
        averageDemand /= seasonalityFactor;

        for (int week = 0; week < weeksToForecast; week++) {
            double weeklyDemand = averageDemand * seasonalityFactor;
            // Add some random variation (real system would use more sophisticated model)
            // weeklyDemand *= (0.9 + 0.2 * Math.random());
            forecast[week] = (int) Math.max(weeklyDemand, averageDemand);
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
        int[] sales1 = {100, 110, 105, 115, 120, 125, 130, 135}; // 8 weeks of sales
        int currentInv1 = 150;
        int[] forecast1 = forecastInventory(sales1, 4, "regular");

        System.out.println("Test 1 - Next 4 weeks forecast:");
        for (int i = 0; i < forecast1.length; i++) {
            System.out.println("Week " + (i + 1) + ": " + forecast1[i] + " units");
        }

        // Test Case 2: Holiday season
        int[] sales2 = {200, 180, 190, 210, 220, 230};
        int[] forecast2 = forecastInventory(sales2, 3, "holiday");

        System.out.println("\nTest 2 - Holiday season forecast:");
        for (int i = 0; i < forecast2.length; i++) {
            System.out.println("Week " + (i + 1) + ": " + forecast2[i] + " units");
        }

        // Test Case 3: Holiday season
        int[] sales3 = {200, 180, 190, 210, 220, 230};
        int[] forecast3 = forecastInventoryMy(sales3, 3, "holiday");

        System.out.println("\nTest 3 - Holiday season forecast:");
        for (int i = 0; i < forecast3.length; i++) {
            System.out.println("Week " + (i + 1) + ": " + forecast3[i] + " units");
        }

        // Test Case 4: Holiday season
        int[] sales4 = {200, 180, 190, 210, 220, 230};
        int[] forecast4 = forecastInventorySimplified(sales4, 3, "holiday");

        System.out.println("\nTest 4 - Holiday season forecast:");
        for (int i = 0; i < forecast4.length; i++) {
            System.out.println("Week " + (i + 1) + ": " + forecast4[i] + " units");
        }
    }
}
