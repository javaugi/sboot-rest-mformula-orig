/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.utils.common;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/*
When to choose ForkJoinPool: You have a computationally intensive problem that can be recursively broken down into smaller
    pieces. Do not use it for I/O.
 */
public class ForkJoinExample extends RecursiveTask<Long> {

    private final long n;

    public ForkJoinExample(long n) {
        System.out.println("ForkJoinExample n=" + n);
        this.n = n;
    }

    @Override
    protected Long compute() {
        // Base case: don't split further
        if (n <= 1) {
            return n;
        }
        // Recursive case: split into two sub-tasks
        System.out.println("ForkJoinExample firstTask ...");
        ForkJoinExample firstTask = new ForkJoinExample(n - 1);
        firstTask.fork(); // Schedule asynchronously

        System.out.println("ForkJoinExample secondTask ...");
        ForkJoinExample secondTask = new ForkJoinExample(n - 2);
        Long secondResult = secondTask.compute(); // Compute inline
        Long firstResult = firstTask.join(); // Wait for the first task's result

        System.out.println("ForkJoinExample results =" + (firstResult + secondResult));
        return firstResult + secondResult;
    }

    public static void main(String[] args) {
        ForkJoinPool pool = new ForkJoinPool();
        System.out.println("Running ForkJoinPool with 10");
        long result = pool.invoke(new ForkJoinExample(10));
        System.out.println("Fibonacci of 10 is: " + result); // Output: 55
    }
}

/*
1. ForkJoinPool: For CPU-Intensive, Recursive Work
    Use Case: The ForkJoinPool is designed for efficient execution of tasks that can be broken down recursively into smaller sub-tasks
        (divide-and-conquer algorithms). It uses a work-stealing algorithm to maximize CPU utilization.

    Ideal for: Sorting (quicksort, mergesort), matrix multiplication, processing large trees or graphs, parallel streams (parallelStream()
        uses the common ForkJoinPool under the hood).

    Characteristics:
        CPU-Bound: The tasks should be computation-heavy, not I/O-bound.
        Blocking is fatal: If a task blocks (e.g., on I/O), it stalls its worker thread and can severely degrade performance.
        Recursive: The problem must be amenable to being split into smaller parts.

Java Code Example: Calculating Fibonacci Recursively (a classic example)
 */
