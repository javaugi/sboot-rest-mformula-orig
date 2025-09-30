/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.interview.hrank.wmart;

/**
 * Problem: Optimize delivery routes for multiple stores to minimize total
 * distance Similar to Walmart's logistics optimization
 */
public class DeliveryRouteOptimization {

    public static int minDeliveryDistance(int[][] distances, int[] storeDemands, int truckCapacity) {
        int n = distances.length; // n stores + warehouse (index 0 is warehouse)
        boolean[] visited = new boolean[n];
        visited[0] = true; // warehouse is starting point

        return backtrack(0, visited, distances, storeDemands, truckCapacity, 0, 1, 0);
    }

    private static int backtrack(
            int current,
            boolean[] visited,
            int[][] distances,
            int[] demands,
            int capacity,
            int currentLoad,
            int count,
            int totalDistance) {
        if (count == visited.length) {
            return totalDistance + distances[current][0]; // Return to warehouse
        }

        int minDistance = Integer.MAX_VALUE;

        for (int next = 1; next < visited.length; next++) {
            if (!visited[next] && currentLoad + demands[next] <= capacity) {
                visited[next] = true;
                int newDistance
                        = backtrack(
                                next,
                                visited,
                                distances,
                                demands,
                                capacity,
                                currentLoad + demands[next],
                                count + 1,
                                totalDistance + distances[current][next]);
                minDistance = Math.min(minDistance, newDistance);
                visited[next] = false;
            }
        }

        // Return to warehouse if needed and continue
        if (current != 0) {
            int returnDistance
                    = backtrack(
                            0,
                            visited,
                            distances,
                            demands,
                            capacity,
                            0,
                            count,
                            totalDistance + distances[current][0]);
            minDistance = Math.min(minDistance, returnDistance);
        }

        return minDistance;
    }

    // Test cases
    public static void main(String[] args) {
        // Test Case 1: 4 stores + warehouse
        int[][] distances1 = {
            {0, 10, 15, 20, 25}, // warehouse to stores
            {10, 0, 35, 25, 30},
            {15, 35, 0, 30, 40},
            {20, 25, 30, 0, 15},
            {25, 30, 40, 15, 0}
        };
        int[] demands1 = {0, 5, 10, 8, 12}; // warehouse demand is 0
        int capacity1 = 20;
        System.out.println(
                "Test 1 Min Distance: " + minDeliveryDistance(distances1, demands1, capacity1));

        // Test Case 2: Simple case
        int[][] distances2 = {
            {0, 5, 10},
            {5, 0, 8},
            {10, 8, 0}
        };
        int[] demands2 = {0, 7, 5};
        int capacity2 = 10;
        System.out.println(
                "Test 2 Min Distance: " + minDeliveryDistance(distances2, demands2, capacity2));
    }
}
