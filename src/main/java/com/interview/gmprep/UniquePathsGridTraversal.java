/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.interview.gmprep;

import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UniquePathsGridTraversal {

	private static final UniquePathsGridTraversal main = new UniquePathsGridTraversal();

	public static void main(String[] args) {
		int[][] grid = { { 1, 0, 1, 0, 1, 1, 0, 0 }, { 0, 1, 1, 0, 0, 1, 0, 0 }, { 0, 1, 1, 0, 0, 1, 0, 0 },
				{ 0, 1, 1, 0, 0, 1, 0, 0 }, { 0, 1, 1, 0, 0, 1, 0, 0 } };
		log.info("uniquePathsWithObstacles {}", main.uniquePathsWithObstacles(grid));
	}

	// Problem: Count unique paths from top-left to bottom-right of m×n grid with
	// obstacles.
	/*
	 * Key Insight: DP table accumulates paths from top and left Obstacles zero out
	 * certain cells Time/Space: O(m*n) (can be optimized to O(n) space)
	 */
	public int uniquePathsWithObstacles(int[][] grid) {
		if (grid.length == 0 && grid[0].length == 0) {
			return 0;
		}

		int m = grid.length, n = grid[0].length;
		// create dp two-dim array the same dim as grid
		int[][] dp = new int[m][n];

		// top corner with initial value 0 (if same as grid) or 1
		dp[0][0] = grid[0][0] == 1 ? 0 : 1;

		// populate the top second horizontal array row with initial value 0 (if same
		// value as grid) or
		// the cell value on the adjacent left
		for (int i = 1; i < m; i++) {
			dp[i][0] = grid[i][0] == 1 ? 0 : dp[i - 1][0];
		}

		// populate the second left vertical array row with initial value 0 (if same value
		// as grid) or
		// the cell value on the adjacent top
		for (int j = 1; j < n; j++) {
			dp[0][j] = grid[0][j] == 1 ? 0 : dp[0][j - 1];
		}

		log.info("\n Original grid ...");
		printTwoDimArray(grid);
		log.info("\n New DP ...");
		printTwoDimArray(dp);
		for (int i = 1; i < m; i++) {
			for (int j = 1; j < n; j++) {
				// set each cell with 0 (if same value as grid) or the sum of the adjacent
				// cells on the left
				// and the above
				dp[i][j] = grid[i][j] == 1 ? 0 : dp[i - 1][j] + dp[i][j - 1];
			}
		}

		// return the value of the second from bottom diagonal
		log.info("\n Final DP ...");
		printTwoDimArray(dp);
		log.info("\n Return  m {}, n {} Value {}", m, n, dp[m - 1][n - 1]);
		return dp[m - 1][n - 1];
	}

	private void printTwoDimArray(int[][] grid) {
		for (int[] row : grid) {
			log.info("\n {}", Arrays.toString(row));
		}
	}

}
