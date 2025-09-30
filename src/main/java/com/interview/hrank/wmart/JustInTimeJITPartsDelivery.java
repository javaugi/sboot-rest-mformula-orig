/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.interview.hrank.wmart;

import java.util.Arrays;

/**
 * @author javau
 */
public class JustInTimeJITPartsDelivery {

    public static void main(String[] args) {
        int[] demand = {2, 3, 4, 5};
        int[] deliveryDays = {3, 4, 5, 6};
        int deliveryCost = 4;
        int holdingCost = 3;

        System.out.println(
                "Test 1 = " + minDeliveryCost(demand, deliveryDays, holdingCost, deliveryCost));
        System.out.println(
                "Test 2 = " + minDeliveryCostOptimized(demand, deliveryDays, holdingCost, deliveryCost));
        System.out.println(
                "Test 3 = " + minDeliveryCostOptimized2(demand, deliveryDays, holdingCost, deliveryCost));
    }

    public static int minDeliveryCostOptimized2(
            int[] demand, int[] deliveryDays, int holdingCost, int deliveryCost) {
        int n = demand.length;
        int[] dp = new int[n + 1];
        Arrays.fill(dp, Integer.MAX_VALUE);
        dp[0] = 0;

        int rtn = 0;
        for (int i = 1; i <= n; i++) {
            int sumDemand = 0;
            for (int j = i; j >= 1 && (i - j) < deliveryDays.length; j--) {
                sumDemand += dp[j - 1];
                int ijCost = dp[j - 1] + deliveryCost + (i - j) * holdingCost * sumDemand;
                dp[i] = Math.min(dp[i], ijCost);
            }
        }

        rtn = dp[n];
        return rtn;
    }

    /*
  Optimization: dp = dynamic process?
      Prefix Sum: Precompute demand sums to avoid nested loops:
          int[] prefixSum = new int[n+1];
          for (int i = 1; i <= n; i++) prefixSum[i] = prefixSum[i-1] + demand[i-1];
     */
    public static int minDeliveryCostOptimized(
            int[] demand, int[] deliveryDays, int holdingCost, int deliveryCost) {
        int n = demand.length;
        int[] dp = new int[n + 1];
        Arrays.fill(dp, Integer.MAX_VALUE);
        dp[0] = 0;

        for (int i = 1; i <= n; i++) {
            int sumDemand = 0;
            for (int j = i; j >= 1 && (i - j) < deliveryDays.length; j--) {
                sumDemand += demand[j - 1];
                int ijCost = dp[j - 1] + deliveryCost + sumDemand * holdingCost * (i - j);
                dp[i] = Math.min(dp[i], ijCost);
            }
        }
        return dp[n];
    }

    /*
  Problem: Minimize warehouse costs by scheduling parts deliveries to match assembly line consumption (critical for GM's inventory management).
     */
    public static int minDeliveryCost(
            int[] demand, int[] deliveryDays, int holdingCost, int deliveryCost) {
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
