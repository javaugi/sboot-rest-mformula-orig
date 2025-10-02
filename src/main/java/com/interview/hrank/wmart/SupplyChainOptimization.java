/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.interview.hrank.wmart;

import java.util.*;

/**
 * Problem: Optimize supply chain network to minimize costs while meeting store demands
 * Similar to Walmart's distribution center optimization
 */
public class SupplyChainOptimization {

	public static int minSupplyChainCost(int[][] supplierCosts, int[] storeDemands, int[] supplierCapacities,
			int warehouseCapacity) {
		int totalCost = 0;

		int suppliers = supplierCosts.length; // size of the array - two-dim vertical
		int stores = supplierCosts[0].length; // size of each array - two-dim horizonal
												// llength of each array
		System.out.println("minSupplyChainCost suppliers=" + suppliers + "-stores=" + stores);
		// Greedy approach: assign each store to cheapest available supplier
		int[] remainingCapacity = Arrays.copyOf(supplierCapacities, suppliers);
		int warehouseUsed = 0;

		for (int store = 0; store < stores; store++) {
			int bestSupplier = -1;
			int minCost = Integer.MAX_VALUE;

			// Find cheapest available supplier
			for (int supplier = 0; supplier < suppliers; supplier++) {
				if (remainingCapacity[supplier] >= storeDemands[store] && supplierCosts[supplier][store] < minCost) {
					minCost = supplierCosts[supplier][store];
					bestSupplier = supplier;
				}
			}

			if (bestSupplier != -1) {
				// Use supplier
				totalCost += minCost * storeDemands[store];
				remainingCapacity[bestSupplier] -= storeDemands[store];
			}
			else if (warehouseUsed + storeDemands[store] <= warehouseCapacity) {
				// Use warehouse (assume warehouse cost is average of supplier costs)
				int storeFinal = store;
				int avgCost = Arrays.stream(supplierCosts).mapToInt(row -> row[storeFinal]).sum() / suppliers;
				totalCost += avgCost * storeDemands[store];
				warehouseUsed += storeDemands[store];
			}
			else {
				// Cannot fulfill demand
				return -1;
			}
		}

		return totalCost;
	}

	// Test cases
	public static void main(String[] args) {
		// Test Case 1: 3 suppliers, 4 stores
		int[][] costs1 = { { 5, 8, 6, 7 }, // Supplier 1 costs to each store
				{ 6, 7, 5, 8 }, // Supplier 2
				{ 7, 6, 8, 5 } // Supplier 3
		};
		int[] demands1 = { 100, 150, 200, 120 }; // Store demands
		int[] capacities1 = { 300, 250, 280 }; // Supplier capacities
		int warehouseCap1 = 500;

		int cost1 = minSupplyChainCost(costs1, demands1, capacities1, warehouseCap1);
		System.out.println("Test 1 Min Cost: " + cost1);

		// Test Case 2: Edge case - high demand
		int[][] costs2 = { { 10 }, { 12 } };
		int[] demands2 = { 1000 };
		int[] capacities2 = { 100, 150 };
		int warehouseCap2 = 200;

		int cost2 = minSupplyChainCost(costs2, demands2, capacities2, warehouseCap2);
		System.out.println("Test 2 Min Cost: " + cost2); // Should be -1 (cannot fulfill)
	}

}
