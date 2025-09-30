/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.interview.gmprep;

import lombok.extern.slf4j.Slf4j;

/**
 * @author javau
 */
@Slf4j
public class ElectricVehicleEVBatteryPackAssembly {

    private static final ElectricVehicleEVBatteryPackAssembly m
            = new ElectricVehicleEVBatteryPackAssembly();

    public static void main(String[] args) {
        int[] cellEnergy = {30, 40, 35, 50, 60, 66, 55};
        int[] thermalLimit = {4, 5, 5, 6, 7, 7, 6};
        int maxThermal = 9;

        log.info(
                "          maxBatteryEnergy {}", m.maxBatteryEnergy(cellEnergy, thermalLimit, maxThermal));
        log.info(
                "Optimized maxBatteryEnergy {}",
                m.maxBatteryEnergyOptimized(cellEnergy, thermalLimit, maxThermal));
    }

    /*
  3. Electric Vehicle (EV) Battery Pack Assembly
      Problem: Optimize battery cell arrangement to maximize energy density while respecting thermal constraints (key for GM's Ultium platform).
     */
    public int maxBatteryEnergy(int[] cellEnergy, int[] thermalLimit, int maxThermal) {
        int n = cellEnergy.length;
        int[][] dp = new int[n + 1][maxThermal + 1];

        for (int i = 1; i <= n; i++) {
            for (int w = 0; w <= maxThermal; w++) {
                if (thermalLimit[i - 1] <= w) {
                    dp[i][w] = Math.max(dp[i - 1][w], dp[i - 1][w - thermalLimit[i - 1]] + cellEnergy[i - 1]);
                } else {
                    dp[i][w] = dp[i - 1][w];
                }
            }
        }
        return dp[n][maxThermal];
    }

    /*
  Optimization:
      1D DP Array: Reduce space complexity:
     */
    public int maxBatteryEnergyOptimized(int[] cellEnergy, int[] thermalLimit, int maxThermal) {
        if (cellEnergy == null
                || cellEnergy.length == 0
                || thermalLimit == null
                || thermalLimit.length == 0
                || maxThermal == 0) {
            return 0;
        }

        int n = cellEnergy.length;
        int[] dp = new int[maxThermal + 1];

        for (int i = 0; i < n; i++) {
            for (int w = maxThermal; w >= thermalLimit[i]; w--) {
                dp[w] = Math.max(dp[w], dp[w - thermalLimit[i]] + cellEnergy[i]);
            }
        }
        return dp[maxThermal];
    }
}
