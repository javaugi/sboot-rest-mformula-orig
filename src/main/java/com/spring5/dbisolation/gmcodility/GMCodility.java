/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.gmcodility;

import com.interview.common.fibonaccci.FibonacciCalc;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RestController
@RequestMapping("/api/gm")
public class GMCodility {

	private static final GMCodility main = new GMCodility();

	public static void main(String[] args) {
		main.testFib();
		// main.run();
	}

	private void testFib() {
		int n = 50;
		int[] mem = new int[n + 1];

		long timestamps = System.currentTimeMillis();
		log.info("1 Looping starts for n {} current time {}", n, timestamps);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i <= n; i++) {
			sb.append(" " + this.fibRecursive(i));
		}
		log.info("1 Looping Done   for n {} current time {}     at cost {} \n values {}", n, System.currentTimeMillis(),
				(System.currentTimeMillis() - timestamps), sb.toString());

		timestamps = System.currentTimeMillis();
		log.info("2 Looping starts for n {} current time {}", n, timestamps);
		sb = new StringBuilder();
		for (int i = 0; i <= n; i++) {
			sb.append(" " + this.fibOptimized(i, mem));
		}
		log.info("2 Looping Done   for n {} current time {}     at cost {} \n values {}", n, System.currentTimeMillis(),
				(System.currentTimeMillis() - timestamps), sb.toString());
	}

	@Autowired
	VehicleTestResultRepository repo;

	@GetMapping
	public ResponseEntity<Collection<VehicleTestResult>> getTestResults(
			@RequestParam(name = "testType", required = false) String testType,
			@RequestParam(name = "startDate", required = false) LocalDate startDate,
			@RequestParam(name = "endDate", required = false) LocalDate endDate,
			@RequestParam(name = "passed", required = false) Boolean passed) {

		if (startDate != null) {
			if (endDate != null && startDate.isAfter(endDate)) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start Date cannot be after End Date");
			}
		}

		Specification<VehicleTestResult> spec = (root, qry, cb) -> null;

		if (testType != null) {
			spec.and((root, qry, cb) -> cb.equal(root.get("testType"), testType));
		}

		if (startDate != null) {
			spec.and((root, qry, cb) -> cb.greaterThanOrEqualTo(root.get("testDate"), startDate));
		}

		if (endDate != null) {
			spec.and((root, qry, cb) -> cb.lessThanOrEqualTo(root.get("testDate"), endDate));
		}

		if (passed != null) {
			spec.and((root, qry, cb) -> cb.equal(root.get("passed"), passed));
		}

		ResponseEntity.ok(repo.findAll(spec));
		ResponseEntity.status(HttpStatus.FOUND).body(repo.findAll(spec));
		new ResponseEntity<>(repo.findAll(spec), HttpStatus.OK);
		return ResponseEntity.accepted().body(repo.findAll(spec));

		// return ResponseEntity.badRequest().build();
	}

	private void run() {
		int[] A = { -2, 3, 5, 7, 8, 9, -5, -12 };
		int[] B = { 1, 3, 6, 4, 1, 2 };
		int[] C = { 1, 2, 3 };
		int[] D = { -1, -3 };

		/*
		 * log.info("Solution 1"); log.info("\n Value {} from Array {}",
		 * main.firstMissingPositive(A), Arrays.toString(A));
		 * log.info("\n Value {} from Array {}", main.firstMissingPositive(B),
		 * Arrays.toString(B)); log.info("\n Value {} from Array {}",
		 * main.firstMissingPositive(C), Arrays.toString(C));
		 * log.info("\n Value {} from Array {}", main.firstMissingPositive(D),
		 * Arrays.toString(D)); //
		 */

		/*
		 * log.info("Solution 2"); log.info("\n OPtimized Value {} from Array {}",
		 * main.firstMissingPositiveOptimized(A), Arrays.toString(A));
		 * log.info("\n OPtimized Value {} from Array {}",
		 * main.firstMissingPositiveOptimized(B), Arrays.toString(B));
		 * log.info("\n OPtimized Value {} from Array {}",
		 * main.firstMissingPositiveOptimized(C), Arrays.toString(C));
		 * log.info("\n OPtimized Value {} from Array {}",
		 * main.firstMissingPositiveOptimized(D), Arrays.toString(D)); //
		 */
		/*
		 * log.info("\n Run A Return Value {} from Array {}",
		 * Arrays.toString(main.twoSumToTarget(A, 10)), Arrays.toString(A));
		 * log.info("\n Run B Return Value {} from Array {}",
		 * Arrays.toString(main.twoSumToTarget(B, 10)), Arrays.toString(B));
		 * log.info("\n Run C find 9 Return Value {} from Array {}",
		 * Arrays.toString(main.twoSumToTarget(A, 9)), Arrays.toString(A)); //
		 */
		int n = 10;
		int[] mem = new int[n + 1];
		// init(mem);
		log.info("\n Fib 2 ... with array {}", Arrays.toString(mem));
		log.info("\n Run fib Value {}", main.fib(n, mem));
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i <= n; i++) {
			sb.append(" " + fib(i, mem));
		}
		log.info("\n Run fib Results {}", sb.toString());

		log.info("\n FibonacciCalc.runFibonacciBigInteger(n)");
		FibonacciCalc.runFibonacciBigInteger(n);
	}

	private void init(int[] A) {
		for (int i = 0; i < A.length; i++) {
			A[i] = 0;
		}
	}

	// what is the most efficient solution to find smallest positive integer from an
	// integer that is
	// not in an given integer array
	private int firstMissingPositive(int[] A) {
		if (A == null || A.length < 1) {
			return 1;
		}

		Arrays.sort(A);
		int max = A[A.length - 1];

		if (max < 1) {
			return 1;
		}

		List<Integer> list = Arrays.stream(A).boxed().filter(e -> (e > 0)).distinct().toList();
		if (list.isEmpty()) {
			return 1;
		}

		for (int i = 1; i <= max + 1; i++) {
			if (!list.contains(i)) {
				return i;
			}
		}

		return (max + 1) > 0 ? (max + 1) : 1;
	}

	public int firstMissingPositiveOptimized(int[] nums) {
		int n = nums.length;

		for (int i = 0; i < n; i++) {
			// Keep swapping until the current number is in the correct position or out of
			// range
			while (nums[i] > 0 && nums[i] <= n && nums[nums[i] - 1] != nums[i]) {
				int correctIndex = nums[i] - 1;
				// Swap nums[i] with nums[correctIndex]
				int temp = nums[i];
				nums[i] = nums[correctIndex];
				nums[correctIndex] = temp;
			}
		}

		// After rearrangement, find the first missing positive
		for (int i = 0; i < n; i++) {
			if (nums[i] != i + 1) {
				return i + 1;
			}
		}

		return n + 1;
	}

	// Given an array of integers nums and an integer target, return the indices of the
	// two numbers
	// that add up to target.
	private int[] twoSumToTarget(int[] nums, int target) {
		Map<Integer, Integer> numToIndex = new HashMap<>();

		for (int i = 0; i < nums.length; i++) {
			if (nums[i] > target) {
				continue;
			}
			int complement = target - nums[i];
			if (numToIndex.containsKey(complement)) {
				log.info("Fopund {} from index {} value {} and index {} value {}", target, i, nums[i],
						numToIndex.get(complement), complement);
				return new int[] { numToIndex.get(complement), i };
			}
			numToIndex.put(nums[i], i);
		}

		return new int[] { 0, 0 };
	}

	// Reverse a stringh
	public String reverse(String s) {
		return new StringBuilder(s).reverse().toString();
	}

	public boolean isPalindrom(String s) {
		return s.endsWith(reverse(s));
	}

	public int fib(int n, int[] memo) {
		if (n <= 1) {
			return n;
		}
		if (memo[n] == 0) {
			memo[n] = fib(n - 1, memo) + fib(n - 2, memo);
		}
		return memo[n];
	}

	public boolean isValidParentheses(String s) {
		Stack<Character> stack = new Stack<>();
		for (char c : s.toCharArray()) {
			if (c == '(') {
				stack.push(')');
			}
			else if (c == '{') {
				stack.push('}');
			}
			else if (c == '[') {
				stack.push(']');
			}
			else if (stack.isEmpty() || stack.pop() != c) {
				return false;
			}
		}
		return stack.isEmpty();
	}

	/*
	 * How It Works: Push closing brackets: When seeing an opening bracket (, {, or [,
	 * push the corresponding closing bracket ), }, or ] to the stack.
	 * 
	 * Example: For "(", push ')' to the stack → Stack becomes [')'] Pop and compare: When
	 * encountering a closing bracket: stack.pop() retrieves the expected closing bracket
	 * Checks if it matches the actual closing bracket c Mismatch Example: Stack has ['}']
	 * but c = ')' → Invalid
	 * 
	 * Edge Cases: stack.isEmpty() handles excess closing brackets (e.g., ")") Final
	 * stack.isEmpty() checks for unclosed brackets (e.g., "(")
	 * 
	 * Example Walkthrough: Input: "()[]{}" '(' → Push ')' → Stack: [')'] ')' → Pop ')' ==
	 * ')' → Valid → Stack: [] '[' → Push ']' → Stack: [']'] ']' → Pop ']' == ']' → Valid
	 * → Stack: [] '{' → Push '}' → Stack: ['}'] '}' → Pop '}' == '}' → Valid → Stack: []
	 * Result: true (stack empty, all pairs matched)
	 * 
	 * Key Notes: Time Complexity: O(n) – Single pass through the string Space Complexity:
	 * O(n) – Worst case (e.g., "(((((") Variations: Can also use a Map to store bracket
	 * pairs for cleaner code:
	 */
	public int fibRecursive(int n) {
		if (n <= 1) {
			return n;
		}

		return fibRecursive(n - 1) + fibRecursive(n - 2);
	}

	public int fibOptimized(int n, int[] mem) {
		if (n <= 1) {
			return n;
		}

		if (mem[n] == 0) {
			mem[n] = fibOptimized(n - 1, mem) + fibOptimized(n - 2, mem);
		}

		return mem[n];
	}

	public class BracketValidator {

		private static final Map<Character, Character> PAIRS = Map.of('(', ')', '{', '}', '[', ']');

		public boolean isValid(String s) {
			Stack<Character> stack = new Stack<>();

			for (char c : s.toCharArray()) {
				if (PAIRS.containsKey(c)) {
					// If it's an opening bracket, push its corresponding closing bracket
					stack.push(PAIRS.get(c));
				}
				else if (stack.isEmpty() || stack.pop() != c) {
					// If it's a closing bracket without a match
					return false;
				}
			}

			return stack.isEmpty();
		}

	}

}
/*
 * // System.out.println("this is a debug message"); import java.util.*;
 * 
 * class Solution { public int solution(int[] A) { // Implement your solution here
 * Arrays.sort(A); int min = A[0]; int max = A[A.length - 1];
 * 
 * List<Integer> list = Arrays.stream(A).boxed().toList();
 * 
 * for (int i = 1; i < max + 1; i++) { if (!list.contains(i)) { return i; } } return (max
 * + 1) > 0 ? (max + 1) : 1; } }
 */

/*
 * // System.out.println("this is a debug message"); import java.util.*;
 * 
 * class Solution { public int solution(int[] A) { // Implement your solution here
 * Arrays.sort(A); int min = A[0]; int max = A[A.length - 1];
 * 
 * List<Integer> list = Arrays.stream(A).boxed().toList();
 * 
 * for (int i = 1; i < max + 1; i++) { if (!list.contains(i)) { return i; } } return (max
 * + 1) > 0 ? (max + 1) : 1; } }
 */

/*
 * Preparing for a whiteboard interview (coding on a board or virtual editor) requires a
 * different approach than regular coding. Here’s a step-by-step guide to ace it:
 * 
 * 1. Understand the Problem (Clarify First!) Ask questions before jumping into code:
 * 
 * "Can you give an example input/output?"
 * 
 * "Are there edge cases I should consider?"
 * 
 * "Should I optimize for time or space complexity?"
 * 
 * Repeat the problem in your own words to confirm understanding.
 * 
 * 2. Plan Your Approach (Think Aloud) Pseudocode first: Write rough steps in plain
 * English.
 * 
 * Discuss trade-offs:
 * "I could use a hash map for O(1) lookups, but it’ll use O(n) space."
 * 
 * Mention alternatives:
 * "A brute-force solution would work, but I can optimize it with two pointers."
 * 
 * 3. Write Clean, Structured Code Use proper indentation and naming (even on a
 * whiteboard).
 * 
 * Break into small functions (even if stubs).
 * 
 * Leave space for adjustments (don’t cram code).
 * 
 * Example: FizzBuzz
 * 
 * python def fizzbuzz(n): for i in range(1, n+1): if i % 15 == 0: # Check 3 and 5 first!
 * print("FizzBuzz") elif i % 3 == 0: print("Fizz") elif i % 5 == 0: print("Buzz") else:
 * print(i) 4. Test with Examples Walk through your code with a sample input.
 * 
 * Check edge cases:
 * 
 * Empty input
 * 
 * Zero/negative numbers
 * 
 * Large inputs
 * 
 * 5. Optimize (If Time Permits) Discuss bottlenecks:
 * "This runs in O(n²) time—can we do better?"
 * 
 * Improve iteratively: "We could memoize results to reduce duplicate calculations."
 * 
 * 6. Communicate Clearly Think aloud: Explain your thought process.
 * 
 * Admit unknowns: "I’m not sure about this syntax, but the logic is..."
 * 
 * Stay calm: If stuck, ask for hints.
 * 
 * Whiteboard Cheat Sheet Step Key Actions Clarify Ask questions, give examples. Plan
 * Pseudocode, discuss trade-offs. Code Write clean, modular code. Test Walk through
 * examples, check edge cases. Optimize Discuss time/space complexity improvements.
 * Practice Problems Easy: Reverse a string, FizzBuzz, Fibonacci.
 * 
 * Medium: Two Sum, Valid Parentheses, Merge Intervals.
 * 
 * Hard: LRU Cache, Trapping Rain Water.
 * 
 * Tip: Practice on a real whiteboard or Excalidraw to simulate the experience.
 * 
 * Final Advice Practice aloud: Explain your code to a rubber duck or friend.
 * 
 * Time yourself: Spend ≤5 mins planning before coding.
 * 
 * Mock interviews: Use Pramp or interviewing.io.
 * 
 * You’ve got this! 💪 Would you like a mock problem to try?
 * 
 * Could you give me java code example for whiteboard interview? Here’s a Java whiteboard
 * interview example with step-by-step explanations for solving "Two Sum" (a classic
 * problem), formatted for clarity on a whiteboard:
 * 
 * Problem Statement Given an array of integers nums and an integer target, return the
 * indices of the two numbers that add up to target.
 * 
 * Example:
 * 
 * java Input: nums = [2, 7, 11, 15], target = 9 Output: [0, 1] // Because nums[0] +
 * nums[1] = 2 + 7 = 9 Step 1: Clarify the Problem (Ask Questions)
 * "Can there be negative numbers?" → Yes.
 * 
 * "Is the array sorted?" → Not necessarily.
 * 
 * "What if there’s no solution?" → Assume exactly one solution exists.
 * 
 * Step 2: Brute-Force Approach (Start Simple) java public int[] twoSum(int[] nums, int
 * target) { for (int i = 0; i < nums.length; i++) { for (int j = i + 1; j < nums.length;
 * j++) { if (nums[i] + nums[j] == target) { return new int[]{i, j}; } } } throw new
 * IllegalArgumentException("No solution"); } Time Complexity: O(n²) Space Complexity:
 * O(1)
 * 
 * Discuss Trade-offs:
 * "This works but is inefficient for large arrays. We can optimize using a hash map."
 * 
 * Step 3: Optimized Solution (Hash Map) java import java.util.HashMap; import
 * java.util.Map;
 * 
 * public int[] twoSum(int[] nums, int target) { Map<Integer, Integer> numToIndex = new
 * HashMap<>();
 * 
 * for (int i = 0; i < nums.length; i++) { int complement = target - nums[i]; if
 * (numToIndex.containsKey(complement)) { return new int[]{numToIndex.get(complement), i};
 * } numToIndex.put(nums[i], i); } throw new IllegalArgumentException("No solution"); }
 * Time Complexity: O(n) Space Complexity: O(n)
 * 
 * Key Points:
 * 
 * One-pass: Checks and adds to the map simultaneously.
 * 
 * Edge Cases: Handles duplicates (later entries overwrite earlier ones, but the solution
 * is found before that matters).
 * 
 * Step 4: Test with Examples java Input: nums = [3, 2, 4], target = 6 Output: [1, 2] // 2
 * + 4 = 6 Walkthrough:
 * 
 * i=0: 3 → complement=3 (not in map) → map={3:0}
 * 
 * i=1: 2 → complement=4 (not in map) → map={3:0, 2:1}
 * 
 * i=2: 4 → complement=2 (found at index 1) → return [1, 2]
 * 
 * Step 5: Discuss Optimizations Follow-up: "What if the array is sorted?" → Use two
 * pointers for O(1) space:
 * 
 * java public int[] twoSumSorted(int[] nums, int target) { int left = 0, right =
 * nums.length - 1; while (left < right) { int sum = nums[left] + nums[right]; if (sum ==
 * target) { return new int[]{left, right}; } else if (sum < target) { left++; } else {
 * right--; } } throw new IllegalArgumentException("No solution"); } Whiteboard Tips for
 * Java Write Cleanly:
 * 
 * Use clear variable names (numToIndex instead of map).
 * 
 * Highlight imports (HashMap).
 * 
 * Explain Annotations:
 * 
 * java // Key: Number, Value: Its index Map<Integer, Integer> numToIndex = new
 * HashMap<>(); Practice Common Patterns:
 * 
 * Hash maps (for O(1) lookups).
 * 
 * Two pointers (for sorted arrays).
 * 
 * Sliding window (for subarrays).
 * 
 * Other Classic Java Problems Reverse a String:
 * 
 * java public String reverse(String s) { return new
 * StringBuilder(s).reverse().toString(); } Fibonacci (Memoization):
 * 
 * java int fib(int n, int[] memo) { if (n <= 1) return n; if (memo[n] == 0) { memo[n] =
 * fib(n-1, memo) + fib(n-2, memo); } return memo[n]; } Valid Parentheses (Stack):
 * 
 * java boolean isValid(String s) { Stack<Character> stack = new Stack<>(); for (char c :
 * s.toCharArray()) { if (c == '(') stack.push(')'); else if (c == '{') stack.push('}');
 * else if (c == '[') stack.push(']'); else if (stack.isEmpty() || stack.pop() != c) {
 * return false; } } return stack.isEmpty(); } Final Advice Practice on Paper/Whiteboard
 * (syntax matters less than logic).
 * 
 * Mention Big-O for every solution.
 * 
 * Ask for feedback during the interview.
 */
