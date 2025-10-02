/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.interview.hrank.wmart;

import java.util.*;

public class ProductDeliveryOptimization {

	// Optimize delivery using knapsack DP
	public static int optimizeDelivery(int[] sizes, int[] demands, int capacity) {
		int n = sizes.length;
		int[][] dp = new int[n + 1][capacity + 1];

		// Dynamic programming: maximize demand within capacity
		for (int i = 1; i <= n; i++) {
			int size = sizes[i - 1];
			int demand = demands[i - 1];
			for (int cap = 0; cap <= capacity; cap++) {
				if (size <= cap) {
					// Option 1: include product
					dp[i][cap] = Math.max(dp[i - 1][cap], dp[i - 1][cap - size] + demand);
				}
				else {
					// Option 2: exclude product
					dp[i][cap] = dp[i - 1][cap];
				}
			}
		}

		return dp[n][capacity];
	}

	// Trace back the chosen products
	public static List<Integer> chosenProducts(int[] sizes, int[] demand, int capacity) {
		int n = sizes.length;
		int[][] dp = new int[n + 1][capacity + 1];

		// Fill DP table
		for (int i = 1; i <= n; i++) { // loop over each product
			int size = sizes[i - 1]; // product size (space it takes)
			int value = demand[i - 1]; // product value (demand)
			for (int cap = 0; cap <= capacity; cap++) { // loop over shelf capacities
				if (size <= cap) {
					// dp[i][cap] = Math.max(dp[i - 1][cap], dp[i - 1][cap - size] +
					// value);

					// Option A: include product i
					int include = dp[i - 1][cap - size] + value;

					// Option B: exclude product i
					int exclude = dp[i - 1][cap];

					// Take the better option
					dp[i][cap] = Math.max(exclude, include);
				}
				else {
					// Can't fit this product at current cap
					dp[i][cap] = dp[i - 1][cap];
				}
			}
		}

		// Backtrack
		List<Integer> chosen = new ArrayList<>();
		int cap = capacity;
		for (int i = n; i > 0; i--) { // go backwards through products
			if (dp[i][cap] != dp[i - 1][cap]) {
				// This product was chosen!
				chosen.add(i - 1); // store index of product

				// Reduce remaining capacity
				cap -= sizes[i - 1];
			}
		}
		Collections.reverse(chosen);
		Collections.sort(chosen, Collections.reverseOrder());
		return chosen;
	}

	public static int maxProductsStored(int[] sizes, int demands[], int capacity) {
		int returnValue = 0;

		int[][] dp = new int[sizes.length + 1][capacity + 1];

		for (int i = 1; i < sizes.length + 1; i++) {
			int size = sizes[i - 1];
			int demand = demands[i - 1];
			for (int cap = 0; cap < capacity + 1; cap++) {
				if (cap <= size) {
					dp[i][cap] = Math.max(dp[i - 1][cap], dp[i - 1][cap - size] + demand);
				}
				else {
					dp[i][cap] = dp[i - 1][cap];
				}
			}
		}

		returnValue = dp[sizes.length][capacity];
		return returnValue;
	}

	// Test
	public static void main(String[] args) {
		// Walmart-like scenario
		int[] productSize = { 2, 3, 4 }; // product sizes
		int[] dailyDemand = { 3, 4, 5 }; // daily demand (value)
		int shelfCapacity = 5; // shelf capacity

		int maxDemand = optimizeDelivery(productSize, dailyDemand, shelfCapacity);
		List<Integer> chosen = chosenProducts(productSize, dailyDemand, shelfCapacity);

		System.out.println("Max demand served: " + maxDemand);
		System.out.println("Chosen products (by index): " + chosen);

		for (int i : chosen) {
			System.out.println("Product size: " + productSize[i] + " cubic ft, demand: " + dailyDemand[i]);
		}
		/*
		 * Max demand served: 7 Chosen products (by index): [0, 1] Product size: 2 cubic
		 * ft, demand: 3 Product size: 3 cubic ft, demand: 4
		 */
	}

}
