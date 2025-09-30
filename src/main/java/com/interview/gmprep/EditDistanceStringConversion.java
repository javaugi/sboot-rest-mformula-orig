/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.interview.gmprep;

// Problem: Find minimum operations (insert, delete, replace) to convert string A to B.
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EditDistanceStringConversion {

    private static final EditDistanceStringConversion main = new EditDistanceStringConversion();

    public static void main(String[] args) {
        log.info("minDistabnce {}", main.minDistance("jhfjfdfgtutu", "welcome"));
    }

    // Problem: Find minimum operations (insert, delete, replace) to convert string A to B.
    /*
  Key Insight:
      DP table compares all prefixes of both strings
      Diagonal moves represent matches or substitutions
      Time/Space: O(m*n)
     */
    public int minDistance(String word1, String word2) {
        if (word1 == null || word1.length() == 0 || word2 == null || word2.length() == 0) {
            return 0;
        }

        int m = word1.length(), n = word2.length();
        int[][] dp = new int[m + 1][n + 1];

        for (int i = 0; i <= m; i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= n; j++) {
            dp[0][j] = j;
        }

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (word1.charAt(i - 1) == word2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(dp[i - 1][j - 1], Math.min(dp[i - 1][j], dp[i][j - 1]));
                }
            }
        }

        return dp[m][n];
    }
}
