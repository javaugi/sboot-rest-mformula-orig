/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.interview.hrank.amz;

import java.util.Stack;

/**
 * @author javau
 */
public class RemoveKDigitsForSmallest {

	public static void main(String[] args) {
		String num = "1432219";
		int k = 3;
	}

	private static String findSmallest(String num, int k) {
		if (k >= num.length()) {
			return "0";
		}

		StringBuilder results = new StringBuilder();

		Stack<Character> stack = new Stack<>();
		for (char c : num.toCharArray()) {
			// remove all larger digits
			while (k > 0 && !stack.isEmpty() && stack.peek() > c) {
				stack.pop();
				k--;
			}
			stack.push(c);
		}

		// remove remaing k from right if needed
		while (k > 0) {
			stack.pop();
			k--;
		}

		// create a return string
		while (!stack.isEmpty()) {
			results.insert(k, stack.pop());
		}

		// remove leading zero
		if (results.length() > 1 && results.charAt(0) == '0') {
			results.deleteCharAt(0);
		}

		return results.toString();
	}

}
