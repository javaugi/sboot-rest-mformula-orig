/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.interview.gmprep;

import lombok.extern.slf4j.Slf4j;

/*
This problem appears to be about finding the minimum cost to assemble a product when you can switch between two assembly 
    lines (A and B), with each switch having a cost (X for A→B and Y for B→A). Here's a dynamic programming solution:

Problem Understanding:
    We have two assembly lines A and B with costs for each station
    Switching from A to B costs X
    Switching from B to A costs Y
    Need to find the minimum total cost to assemble through all stations
 */
@Slf4j
public class AssemblyLineParallelStationsWithFixedTransfer {
    
    private static final AssemblyLineParallelStationsWithFixedTransfer main = new AssemblyLineParallelStationsWithFixedTransfer();
    
    public static void main(String[] args) {
        int[] A = {5,3,4,8,9,12,7,4};
        int X = 8;
        int[] B = {3,7,5,10,8,3,7,3};
        int Y = 4;
        log.info("1 The best solution {}", main.solutionOptimized(A, B, X, Y));
    }

    public int solutionOptimized(int[] A, int[] B, int X, int Y) {
        if (A == null || B == null || A.length != B.length || A.length == 0) {
            return 0;
        }

        int prevA = A[0];
        int prevB = B[0];

        for (int i = 1; i < A.length; i++) {
            int currA = A[i] + Math.min(prevA, prevB + Y);
            int currB = B[i] + Math.min(prevB, prevA + X);

            prevA = currA;
            prevB = currB;
        }

        log.info("The best A {} B {}", prevA, prevB);
        return Math.min(prevA, prevB);
    }
    
    public int solution(int[] A, int[] B, int X, int Y) {
        if (A == null || B == null || A.length != B.length || A.length == 0) {
            return 0; // or throw exception based on requirements
        }

        int n = A.length;
        // dpA[i] - min cost to reach station i in line A
        // dpB[i] - min cost to reach station i in line B
        int[] dpA = new int[n];
        int[] dpB = new int[n];

        // Base cases
        dpA[0] = A[0]; // Start at line A
        dpB[0] = B[0]; // Start at line B

        for (int i = 1; i < n; i++) {
            // Cost to reach station i in line A:
            // Either continue on A or switch from B to A
            dpA[i] = A[i] + Math.min(dpA[i - 1], dpB[i - 1] + Y);

            // Cost to reach station i in line B:
            // Either continue on B or switch from A to B
            dpB[i] = B[i] + Math.min(dpB[i - 1], dpA[i - 1] + X);
        }

        // The answer is the minimum of last station in A or B
        return Math.min(dpA[n - 1], dpB[n - 1]);
    }
}
