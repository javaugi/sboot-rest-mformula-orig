/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.interview.gmprep;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MaxSubarrayKadaneAlgorithm {

	private static final MaxSubarrayKadaneAlgorithm main = new MaxSubarrayKadaneAlgorithm();

	public static void main(String[] args) {
		main.maxSubarrayKadaneAlgorithm();
	}

	// Problem: Find the contiguous subarray with the largest sum.
	private void maxSubarrayKadaneAlgorithm() {
		int[] nums = { 2, 12, 13, 17, 23, 73, 58, 90, 55, 64, 5 };
		log.info("maxSubArray {}", this.maxSubArray(nums));
	}

	/*
	 * Key Insight: At each point, decide whether to start new subarray or continue
	 * previous Maintains two variables tracking current and global maxima Time: O(n),
	 * Space: O(1)
	 */
	public int maxSubArray(int[] nums) {
		if (nums == null || nums.length == 0) {
			return 0;
		}

		int maxCurrent = nums[0];
		int maxGlobal = nums[0];

		for (int i = 1; i < nums.length; i++) {
			maxCurrent = Math.max(nums[i], maxCurrent + nums[i]);
			maxGlobal = Math.max(maxGlobal, maxCurrent);
		}

		return maxGlobal;
	}

}
