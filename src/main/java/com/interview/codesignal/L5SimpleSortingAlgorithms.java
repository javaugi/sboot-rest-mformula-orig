/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.interview.codesignal;

import java.util.Arrays;
import java.util.Comparator;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author javaugi
 */
/*
Simple Sorting Algorithms with Java
Lesson Overview
Welcome to the practice-based lesson dedicated to Simple Sorting Algorithms. Sorting is one of the most investigated classes of algorithms 
in computer science. Understanding different methods of sorting becomes more crucial as data size increases. In this lesson, we are going
to revisit basic sorting algorithms: Bubble, Selection, and Insertion sorts. These are not only excellent exercises for those new to 
coding, but they also lay the groundwork for more complex sorting algorithms like QuickSort.

Quick Look at QuickSort
Before we dive into the basics, let's peek into more complex territory by examining QuickSort, a popular divide-and-conquer sorting algorithm. 
The idea behind it is to pick a pivot element from the array and partition the other elements into two arrays according to whether they are 
less than or greater than the pivot. The pivot is then placed in its correct sorted position between the two subarrays. This process is 
recursively applied to each of the two arrays around the pivot, leading to a sorted array.

Here's how you can implement QuickSort in Java:

What's Next: Back to Basics!
On top of QuickSort, we will reverse gears and return to the simple sorting algorithms — Bubble Sort, Selection Sort, and Insertion Sort.
These foundational algorithms will not only reinforce your understanding of the sorting principle but are often used as a stepping stone 
to understanding more complex algorithms. Happy learning, and let's sort it out!
 */
@Slf4j
public class L5SimpleSortingAlgorithms {

    public static void quickSort(int[] arr) {
        if (arr == null || arr.length == 0) {
            return;
        }
        
        quickSort(arr, 0, arr.length - 1);
    }
    
    public static void quickSort(int[] arr, int low, int high) {
        if (low < high) {
            int pivotElement = partition(arr, low, high);
            // Recursively sort elements before partition and after partition
            quickSort(arr, low, pivotElement - 1);
            quickSort(arr, pivotElement + 1, high);
        }
    }

    private static int partition(int[] arr, int low, int high) {
        int highValue = arr[high];
        int i = (low - 1); // Index of smaller element
        for (int j = low; j < high; j++) {
            // If current element is smaller than or equal to pivot
            if (arr[j] <= highValue) {
                i++;

                // Swap arr[i] and arr[j]
                swap(arr, i, j);
            }
        }

        // Swap arr[i+1] and arr[high] (or pivot)
        int rtnValue = i + 1;
        swap(arr, rtnValue, high);

        return rtnValue;
    }
    
    private static void swap(int[] array, int i, int j) {
        int tmp = array[i];
        array[i] = array[j];
        array[j] = tmp;
    }    

    public static void main(String[] args) {
        int[] arr = {10, 7, 8, 9, 1, 5};

        System.out.println("\nOriginal array: ");
        printArray(arr);

        System.out.println("\n Sorting array: high=" + (arr.length - 1));
        quickSort(arr);
        //quickSort(arr, 0, arr.length - 1);

        System.out.println("\nSorted array: ");
        printArray(arr);

        System.out.println("\n\n new same as the previous Original array: ");
        int[] arr1 = {10, 7, 8, 9, 1, 5};
        printArray(arr1);
        arr1 = Arrays.stream(arr1).sorted().toArray();
        System.out.println("\n new same as the previous  Sorted array: ");
        printArray(arr1);
        System.out.println("\n Done printing new same as the previous  Sorted array: ");

        int[] arr2 = {10, 7, 8, 9, 1, 5, 3};
        System.out.println("\nO= Original array: " + Arrays.toString(arr2));
        Arrays.sort(arr2);
        System.out.println("\nO= Sorted array: " + Arrays.toString(arr2));
        
        log.info("Two Dim Sorting");
        int[][] matrix = {{4, 7, 8}, {10, 8, 9}, {6, 4, 8}, {10, 6, 8}, {5, 8, 7}, {5, 12, 13}};
        sortingTwoDimArray(matrix);
        int[][] matrix2 = {{4, 7, 8}, {10, 8, 9}, {6, 4, 8}, {10, 6, 8}, {5, 8, 7}, {5, 12, 13}};
        sortingTwoDimArray2(matrix2);
        int[][] matrix3 = {{4, 7, 8}, {10, 8, 9}, {6, 4, 8}, {10, 6, 8}, {5, 8, 7}, {5, 12, 13}};
        sortingTwoDimArray2(matrix3);
    }
    
    private static void printArray(int[] arr) {
        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i] + " ");
        }
    }
    
    private static void sortingTwoDimArray(int[][] matrix) {
        log.info("Before sort {}", Arrays.deepToString(matrix));
        //Arrays.sort(matrix, (a, b) -> (b[1] - a[1]));
        Arrays.sort(matrix, (a, b) -> (Integer.valueOf(b[1]).compareTo(a[1])));
        log.info("After sort {}", Arrays.deepToString(matrix));
    }
    
    private static void sortingTwoDimArray2(int[][] matrix) {
        log.info("Before sort {}", Arrays.deepToString(matrix));
        
        int columnIndexToSort = 1; // 0-indexed
        /*
        Arrays.sort(matrix, new Comparator<int[]>() {
            @Override
            public int compare(int[] row1, int[] row2) {
                return Integer.compare(row1[columnIndexToSort], row2[columnIndexToSort]);
            }
        });
        // */
        
        Arrays.sort(matrix, new Comparator<int[]>() {
            @Override
            public int compare(int[] a, int[] b) {
                return b[columnIndexToSort] - a[columnIndexToSort];
            }
        });
        
        log.info("After sort {}", Arrays.deepToString(matrix));
    }
   
    
    private static void sortingTwoDimArray3(int[][] matrix) {
        log.info("Before sort {}", Arrays.deepToString(matrix));
        
        int columnIndexToSort = 1; // 0-indexed
        Arrays.sort(matrix, new Comparator<int[]>() {
            @Override
            public int compare(int[] a, int[] b) {
                return Integer.compare(b[columnIndexToSort], a[columnIndexToSort]);
            }
        });
        
        log.info("After sort {}", Arrays.deepToString(matrix));
    }    
}

/*
Arrays.sort(tasks, ...)

Arrays.sort() is a static method from the java.util.Arrays utility class. It's used to sort arrays.

When provided with two arguments (an array and a Comparator), it sorts the array elements according to the rules defined by the Comparator.

tasks is the array being sorted. Given the comparator, tasks is almost certainly a 2D array, specifically an array of int[] or long[] 
    (e.g., int[][] tasks). Each inner array a or b represents a "task," and a[1] refers to the element at index 1 within that inner array.

(a, b) -> (b[1] - a[1])

This is a lambda expression in Java, introduced in Java 8. It's a concise way to implement a functional interface, in this case, java.util.Comparator<T>.

A Comparator defines how two objects (a and b) should be compared for sorting purposes. Its compare method (which this lambda implements) returns:

A negative integer if a should come before b.

Zero if a and b are considered equal for sorting purposes.

A positive integer if a should come after b.

a and b: These are two elements from the tasks array that are being compared. Since tasks is an array of arrays (e.g., int[][]), a and b themselves are int[] (or long[]).

b[1] - a[1]: This is the core of the comparison logic.

It subtracts the value at index 1 of a from the value at index 1 of b.

If b[1] is greater than a[1]: The result (b[1] - a[1]) will be a positive number. According to the Comparator contract, this means a should come after b.

If b[1] is less than a[1]: The result (b[1] - a[1]) will be a negative number. This means a should come before b.

If b[1] is equal to a[1]: The result will be zero. a and b are considered equal.

What the Code Does (in plain English):
This line of code sorts the tasks array. The sorting is performed based on the second element (at index 1) of each inner array (task).

Crucially, because it's b[1] - a[1] (instead of a[1] - b[1]), it sorts the tasks in descending order of their second element.
*/