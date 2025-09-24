/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.interview.hrank;

/**
 *
 * @author javau
 */
public class BinarySteps {
    public static int stepsToZero(String S) {
        // Convert binary string to integer
        int V = Integer.parseInt(S, 2);
        int steps = 0;
        
        while (V != 0) {
            if ((V & 1) == 0) {   // Even: LSB is 0
                V >>= 1;          // Divide by 2 (right shift)
            } else {              // Odd: LSB is 1
                V -= 1;
            }
            steps++;
        }
        return steps;
    }
    
    public static int stepsToZero2(String S) {
        // Convert binary string to integer
        int V = Integer.parseInt(S, 2);
        int steps = 0;
        
        while (V != 0) {
            if ((V % 2) == 0) {   // Even: LSB is 0
                V >>= 1;          // Divide by 2 (right shift)
            } else {              // Odd: LSB is 1
                --V;
            }
            steps++;
        }
        return steps;
    }

    public static int stepsToZeroRecursive(int V) {
        if (V == 0) return 0;
        return 1 + (V % 2 == 0 ? 
                    stepsToZeroRecursive(V / 2) : 
                    stepsToZeroRecursive(V - 1));
    }    

    public static void main(String[] args) {
        String S = "10110";  // Binary for 6
        System.out.println("1. S=" + S + "-value=" + Integer.parseInt(S, 2));
        System.out.println("2. S=" + S + "-value=" + Integer.valueOf(S, 2));
        
        
        
        System.out.println("1 stepsToZero          =" + stepsToZero(S));   // Output: 7
        System.out.println("2 stepsToZero2         =" + stepsToZero2(S));  // Output: 7
        int V = Integer.parseInt(S, 2);
        System.out.println("3 stepsToZeroRecursive =" + stepsToZeroRecursive(V));  // Output: 7
    }    
}

/*
Problem Statement Recap
We need to process an integer V derived from a binary-encoded string S:

Initial Value: Convert string S to its binary integer representation V.

Operations:

If V is even: Divide V by 2.

If V is odd: Subtract 1 from V.

Termination: Repeat until V becomes 0.

Goal: Return the number of steps taken to reduce V to 0.

Solution Approach
This problem resembles the "Number of Steps to Reduce a Number to Zero" algorithm (LeetCode #1342), but with a binary string input. Here’s how to solve it efficiently:

Steps:
Convert S to Integer V:

Treat S as a binary string (e.g., "110" → 6).

Process V Until Zero:

Use bitwise operations for efficiency.

Count Steps:

Increment a counter for each operation.

Java Implementation
java
public class BinarySteps {
    public static int stepsToZero(String S) {
        // Convert binary string to integer
        int V = Integer.parseInt(S, 2);
        int steps = 0;
        
        while (V != 0) {
            if ((V & 1) == 0) {  // Even: LSB is 0
                V >>= 1;          // Divide by 2 (right shift)
            } else {              // Odd: LSB is 1
                V -= 1;
            }
            steps++;
        }
        return steps;
    }

    public static void main(String[] args) {
        String S = "110";  // Binary for 6
        System.out.println(stepsToZero(S));  // Output: 4
    }
}
Explanation:
Binary Conversion:

Integer.parseInt(S, 2) converts S (e.g., "110") to its decimal value (6).

Bitwise Checks:

V & 1 checks the least significant bit (LSB):

0: Even → Right shift (V >>= 1 divides by 2).

1: Odd → Subtract 1.

Step Counting:

Each operation (division/subtraction) increments steps.

Example Walkthrough
For S = "110" (V = 6):

Step 1: 6 (even) → 6 / 2 = 3 (steps: 1)

Step 2: 3 (odd) → 3 - 1 = 2 (steps: 2)

Step 3: 2 (even) → 2 / 2 = 1 (steps: 3)

Step 4: 1 (odd) → 1 - 1 = 0 (steps: 4)

Result: 4 steps.

Optimization Notes
Bitwise Operations:

Faster than arithmetic (% 2 for even/odd checks).

Time Complexity:

O(log V): Each division roughly halves V.

Edge Cases:

Empty string (S = ""): Return 0.

S = "0": Return 0.

Alternative (Recursive)
java
public static int stepsToZeroRecursive(int V) {
    if (V == 0) return 0;
    return 1 + (V % 2 == 0 ? 
                stepsToZeroRecursive(V / 2) : 
                stepsToZeroRecursive(V - 1));
}
Key Takeaways
Binary Conversion: Use Integer.parseInt(S, 2).

Efficiency: Bitwise ops (&, >>) outperform arithmetic.

Termination: Loop until V = 0.
*/
