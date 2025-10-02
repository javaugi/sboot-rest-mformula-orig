/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.interview.gmprep;

import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;

/*
In the context of algorithm design and computer science, "DP" stands for Dynamic Programming.
    Dynamic Programming (DP) is an algorithmic technique used to solve complex problems by breaking them down into simpler, overlapping
        subproblems.
    A "DP array" (or sometimes called a "DP table") is a data structure, usually an array, used to store the solutions to these smaller
        subproblems. By storing these intermediate results, the algorithm avoids recomputing the same solutions repeatedly, leading to a
        more efficient solution, especially when dealing with problems that have overlapping subproblems.
In summary, "Builds a DP array" means creating and filling a table (usually an array) to store the results of smaller subproblems as part of a
    dynamic programming approach to solve a larger problem.
 */
@Slf4j
public class CoinChangeProblemSolMinimumCoins {

	private static final CoinChangeProblemSolMinimumCoins main = new CoinChangeProblemSolMinimumCoins();

	public static void main(String[] args) {
		// Problem: Given coins of different denominations and a total amount, find the
		// minimum number
		// of coins needed to make up that amount.
		int[] coins = { 1, 2, 5, 20, 30, 100 };
		int amount = 595;
		log.info("\n min {} of coins needed to make {} \n from the coin collections {}", main.coinChange(coins, amount),
				amount, Arrays.toString(coins));
	}

	/*
	 * Key Insight: Builds a DP array where dp[i] represents min coins for amount i For
	 * each coin, updates all amounts that can be reached with that coin Time: O(amount *
	 * n), Space: O(amount)
	 */
	public int coinChange(int[] coins, int amount) {
		if (coins == null || coins.length == 0 || amount == 0) {
			return 0;
		}

		int[] dp = new int[amount + 1];
		// Assigns the specified (amount + 1) int value to each element of the specified
		// dp array of
		// ints.
		Arrays.fill(dp, amount + 1);
		dp[0] = 0;

		for (int coin : coins) {
			for (int i = coin; i <= amount; i++) {
				// For each coin, updates all amounts that can be reached with that coin
				dp[i] = Math.min(dp[i], dp[i - coin] + 1);
			}
		}

		log.info("Final db array {}", Arrays.toString(dp));
		return dp[amount] > amount ? -1 : dp[amount];
	}

}
