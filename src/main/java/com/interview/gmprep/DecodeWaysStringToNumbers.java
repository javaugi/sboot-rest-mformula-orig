/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.interview.gmprep;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DecodeWaysStringToNumbers {
    
    private static final DecodeWaysStringToNumbers main = new DecodeWaysStringToNumbers();
    
    public static void main(String[] args) {
        String str = "274759548";
        log.info("numDecodings {}", main.numDecodings(str));
    }

    //Problem: Count how many ways a digit string can be decoded to letters (A=1, B=2,... Z=26).
    /*
    Key Insight:
        Works backwards to avoid leading zero issues
        Checks both single-digit and two-digit possibilities
        Time: O(n), Space: O(n) (can be O(1) with variable optimization)    
    */
    public int numDecodings(String s) {
        int n = s.length();
        int[] dp = new int[n + 1];
        dp[n] = 1;

        for (int i = n - 1; i >= 0; i--) {
            if (s.charAt(i) != '0') {
                dp[i] = dp[i + 1];
                if (i < n - 1 && Integer.parseInt(s.substring(i, i + 2)) <= 26) {
                    dp[i] += dp[i + 2];
                }
            }
        }

        return dp[0];
    }
}
