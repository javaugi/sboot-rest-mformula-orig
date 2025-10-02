/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.interview.hrank.wmart;

/**
 * Problem: Optimize warehouse shelf space allocation for products with different sizes
 * and demand frequencies Similar to Walmart's inventory management system
 */
public class InventoryOptimization {

	public static int maxProductsStored(int[] sizes, int[] demands, int capacity) {
		int n = sizes.length;
		// Create DP table: rows = products, columns = capacities
		int[][] dp = new int[n + 1][capacity + 1];

		// Build DP table bottom-up
		for (int i = 1; i <= n; i++) {
			int size = sizes[i - 1];
			int demand = demands[i - 1];
			for (int cap = 0; cap <= capacity; cap++) {
				// We can choose to include or exclude current product
				if (size <= cap) {
					// Exclude current product (the first) or Include current product (the
					// second)
					dp[i][cap] = Math.max(dp[i - 1][cap], dp[i - 1][cap - size] + demand);
				}
				else {
					// Current product doesn't fit, must exclude it
					dp[i][cap] = dp[i - 1][cap];
				}
			}
		}

		// Optimal solution for all products and full capacity
		return dp[n][capacity];
	}

	// Test cases
	public static void main(String[] args) {
		// Test Case 1: Basic case by DS
		int[] sizes1 = { 2, 3, 4, 5 }; // product Size
		int[] demand1 = { 3, 4, 5, 6 }; // daily demand
		int capacity1 = 8; // shelf capacity
		System.out.println("\n\n Test 1 "); // Expected: 10
		int value = maxProductsStored(sizes1, demand1, capacity1);
		System.out.println("Test 1 Result: " + value); // Expected: 10

		// Test Case 2: Walmart-like scenario
		int[] sizes2 = { 1, 2, 3, 4, 5 }; // product sizes in cubic feet
		int[] demand2 = { 10, 20, 15, 25, 30 }; // daily demand frequency
		int capacity2 = 10; // shelf capacity
		System.out.println("\n\n Test 2 "); // Expected: 10
		value = maxProductsStored(sizes2, demand2, capacity2);
		System.out.println("Test 2: " + value); // Expected: 65

		// Test Case 3: Edge case
		int[] sizes3 = { 5 };
		int[] demand3 = { 10 };
		int capacity3 = 4;
		System.out.println("\n\n Test 3 "); // Expected: 10
		value = maxProductsStored(sizes3, demand3, capacity3);
		System.out.println("Test 3: " + value); // Expected: 65

		// Test Case 4: Walmart-like scenario
		int[] sizes4 = { 2, 3, 4 }; // product sizes in cubic feet
		int[] demand4 = { 3, 4, 5 }; // daily demand frequency
		int capacity4 = 5; // shelf capacity
		System.out.println("\n\n Test 4 "); // Expected: 10
		value = maxProductsStored(sizes4, demand4, capacity4);
		System.out.println("Test 4: " + value); // Expected: 65
	}

}
/*
 * How each cell is calculated: Row 1 (Product 1, size 2, demand 3): Capacity 0-1: Product
 * doesn't fit → 0 Capacity 2-5: Product fits → max(exclude=0, include=3) = 3 Row 2
 * (Product 2, size 3, demand 4): Capacity 3: max(exclude=3, include=0+4) = 4 Capacity 5:
 * max(exclude=3, include=dp[1][2]+4=3+4=7) = 7 Row 3 (Product 3, size 4, demand 5):
 * Capacity 4: max(exclude=4, include=0+5) = 5 Capacity 5: max(exclude=7,
 * include=dp[2][1]+5=0+5=5) = 7
 * 
 * Key Insights Optimal Substructure: The optimal solution for i products and capacity w
 * builds upon optimal solutions for smaller subproblems.
 * 
 * Two Choices at Each Step: Exclude current product: dp[i-1][w] (carry forward previous
 * best) Include current product: dp[i-1][w - size] + demand (make space + add value) Time
 * Complexity: O(n × capacity) - much better than brute force O(2ⁿ)
 * 
 * Real-world Interpretation This answers:
 * "Given limited shelf space, which combination of products should we stock to maximize total demand fulfillment?"
 * The result dp[n][shelfCapacity] tells us the maximum total demand frequency we can
 * achieve without exceeding the shelf capacity. Final Answer: For our example, we can
 * achieve demand frequency of 7 by taking Product 1 (size 2, demand 3) and Product 2
 * (size 3, demand 4), using exactly capacity 5.
 */
