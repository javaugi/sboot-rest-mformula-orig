/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.interview.hrank.wmart;

/**
 * Problem: Optimize warehouse shelf space allocation for products with
 * different sizes and demand frequencies Similar to Walmart's inventory
 * management system
 */
public class InventoryOptimization {

    public static int maxProductsStored(int[] productSizes, int[] demandFreq, int shelfCapacity) {
        int n = productSizes.length;
        int[][] dp = new int[n + 1][shelfCapacity + 1];

        for (int i = 1; i <= n; i++) {
            for (int w = 0; w <= shelfCapacity; w++) {
                if (productSizes[i - 1] <= w) {
                    dp[i][w] = Math.max(dp[i - 1][w], dp[i - 1][w - productSizes[i - 1]] + demandFreq[i - 1]);
                } else {
                    dp[i][w] = dp[i - 1][w];
                }
            }
        }
        return dp[n][shelfCapacity];
    }

    // Test cases
    public static void main(String[] args) {
        // Test Case 1: Basic case
        int[] sizes1 = {2, 3, 4, 5};
        int[] demand1 = {3, 4, 5, 6};
        int capacity1 = 8;
        System.out.println("Test 1: " + maxProductsStored(sizes1, demand1, capacity1)); // Expected: 10

        // Test Case 2: Walmart-like scenario
        int[] sizes2 = {1, 2, 3, 4, 5}; // product sizes in cubic feet
        int[] demand2 = {10, 20, 15, 25, 30}; // daily demand frequency
        int capacity2 = 10; // shelf capacity
        System.out.println("Test 2: " + maxProductsStored(sizes2, demand2, capacity2)); // Expected: 65

        // Test Case 3: Edge case
        int[] sizes3 = {5};
        int[] demand3 = {10};
        int capacity3 = 4;
        System.out.println("Test 3: " + maxProductsStored(sizes3, demand3, capacity3)); // Expected: 0
    }
}
