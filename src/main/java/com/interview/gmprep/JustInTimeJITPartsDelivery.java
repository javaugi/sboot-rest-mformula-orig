/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.interview.gmprep;

import java.util.Arrays;

/**
 *
 * @author javau
 */
public class JustInTimeJITPartsDelivery {

    /*
    Problem: Minimize warehouse costs by scheduling parts deliveries to match assembly line consumption (critical for GM's inventory management).
    */
    public int minDeliveryCost(int[] demand, int[] deliveryDays, int holdingCost, int deliveryCost) {
        int n = demand.length;
        int[] dp = new int[n + 1];
        Arrays.fill(dp, Integer.MAX_VALUE);
        dp[0] = 0;

        for (int i = 1; i <= n; i++) {
            int sumDemand = 0;
            for (int j = i; j >= 1 && (i - j) < deliveryDays.length; j--) {
                sumDemand += demand[j - 1];
                dp[i] = Math.min(dp[i], dp[j - 1] + deliveryCost + sumDemand * holdingCost * (i - j));
            }
        }
        return dp[n];
    }

    /*
    Optimization:
        Prefix Sum: Precompute demand sums to avoid nested loops:
            int[] prefixSum = new int[n+1];
            for (int i = 1; i <= n; i++) prefixSum[i] = prefixSum[i-1] + demand[i-1];
    */
    public int minDeliveryCostOptimized(int[] demand, int[] deliveryDays, int holdingCost, int deliveryCost) {
        int n = demand.length;
        int[] dp = new int[n + 1];
        Arrays.fill(dp, Integer.MAX_VALUE);
        dp[0] = 0;

        for (int i = 1; i <= n; i++) {
            int sumDemand = 0;
            for (int j = i; j >= 1 && (i - j) < deliveryDays.length; j--) {
                sumDemand += demand[j - 1];
                dp[i] = Math.min(dp[i], dp[j - 1] + deliveryCost + sumDemand * holdingCost * (i - j));
            }
        }
        return dp[n];
    }
}
