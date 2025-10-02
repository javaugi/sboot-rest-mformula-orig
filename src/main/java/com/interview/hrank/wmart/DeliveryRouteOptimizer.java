/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.interview.hrank.wmart;

public class DeliveryRouteOptimizer {

	public static int findMinDeliveryDistance(int[][] distances, int[] storeDemands, int truckCapacity) {
		int totalStores = distances.length;
		boolean[] visitedStores = new boolean[totalStores];
		visitedStores[0] = true; // Mark warehouse as visited (starting point)

		return exploreRoutes(0, visitedStores, distances, storeDemands, truckCapacity, 0, 1, 0);
	}

	private static int exploreRoutes(int currentLocation, boolean[] visitedStores, int[][] distances, int[] demands,
			int truckCapacity, int currentLoad, int visitedCount, int currentDistance) {

		// All stores visited, return to warehouse
		if (visitedCount == visitedStores.length) {
			return currentDistance + distances[currentLocation][0];
		}

		int minDistance = Integer.MAX_VALUE;

		// Try visiting each unvisited store that fits in remaining capacity
		for (int nextStore = 1; nextStore < visitedStores.length; nextStore++) {
			if (canVisitStore(nextStore, visitedStores, demands, currentLoad, truckCapacity)) {
				int distanceToNextStore = distances[currentLocation][nextStore];

				visitedStores[nextStore] = true;
				int newLoad = currentLoad + demands[nextStore];

				int totalDistance = exploreRoutes(nextStore, visitedStores, distances, demands, truckCapacity, newLoad,
						visitedCount + 1, currentDistance + distanceToNextStore);

				minDistance = Math.min(minDistance, totalDistance);
				visitedStores[nextStore] = false; // backtrack
			}
		}

		// Option: return to warehouse to unload and continue deliveries
		if (currentLocation != 0) {
			int returnToWarehouseDistance = distances[currentLocation][0];
			int totalDistance = exploreRoutes(0, visitedStores, distances, demands, truckCapacity, 0, visitedCount,
					currentDistance + returnToWarehouseDistance);
			minDistance = Math.min(minDistance, totalDistance);
		}

		return minDistance;
	}

	private static boolean canVisitStore(int store, boolean[] visitedStores, int[] demands, int currentLoad,
			int truckCapacity) {
		return !visitedStores[store] && (currentLoad + demands[store]) <= truckCapacity;
	}

	// Test cases
	public static void main(String[] args) {
		runTest("Test 1", createTest1Data(), 20);
		runTest("Test 2", createTest2Data(), 10);
	}

	private static void runTest(String testName, TestData testData, int capacity) {
		System.out.printf("%s Min Distance: %d%n", testName,
				findMinDeliveryDistance(testData.distances, testData.demands, capacity));
	}

	private static TestData createTest1Data() {
		int[][] distances = { { 0, 10, 15, 20, 25 }, // warehouse to stores
				{ 10, 0, 35, 25, 30 }, { 15, 35, 0, 30, 40 }, { 20, 25, 30, 0, 15 }, { 25, 30, 40, 15, 0 } };
		int[] demands = { 0, 5, 10, 8, 12 };
		return new TestData(distances, demands);
	}

	private static TestData createTest2Data() {
		int[][] distances = { { 0, 5, 10 }, { 5, 0, 8 }, { 10, 8, 0 } };
		int[] demands = { 0, 7, 5 };
		return new TestData(distances, demands);
	}

	// Helper class to organize test data
	private static class TestData {

		final int[][] distances;

		final int[] demands;

		TestData(int[][] distances, int[] demands) {
			this.distances = distances;
			this.demands = demands;
		}

	}

}
