/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.interview.gmprep;

// Problem: Find the length of the longest subsequence where elements are in increasing order.
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LongestIncreasingSubsequence {

    private static final LongestIncreasingSubsequence main = new LongestIncreasingSubsequence();

    public static void main(String[] args) {
        int[] nums = {3, 14, 5, 63, 7, 8, 9, 4, 23, 3, 54};
        log.info("lengthOfLIS {}", main.lengthOfLIS(nums));
    }

    /*
  Key Insight:
      DP array tracks LIS length ending at each index
      For each element, check all previous elements to extend sequences
      Time: O(n²), Space: O(n)
     */
    public int lengthOfLIS(int[] nums) {
        if (nums == null || nums.length == 0) {
            return 0;
        }

        int[] dp = new int[nums.length];
        Arrays.fill(dp, 1);
        int max = 1;

        for (int i = 1; i < nums.length; i++) {
            for (int j = 0; j < i; j++) {
                if (nums[i] > nums[j]) {
                    dp[i] = Math.max(dp[i], dp[j] + 1);
                }
            }
            max = Math.max(max, dp[i]);
        }

        return max;
    }
}
