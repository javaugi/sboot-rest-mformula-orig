/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.interview.gmprep;

import lombok.extern.slf4j.Slf4j;

/*
1. Assembly Line Scheduling with Parallel Stations
    Problem: Optimize production when some stations can run in parallel (like GM's flexible manufacturing systems).
*/

@Slf4j
public class AssemblyLineSchedulingWithParallelStations {
    private static final AssemblyLineSchedulingWithParallelStations main = new AssemblyLineSchedulingWithParallelStations();
    
    public static void main(String[] args) {
        int[][] stationTime = {{2, 6, 5, 11, 4, 7}, {4, 8, 9, 10, 5, 7}};
        int[][] transferTime = {{2, 3, 2, 4, 3, 1}, {1, 3, 3, 2, 3, 2}};
        int[] entry = {3, 2};
        int[] exit = {2, 4};
        
        
        log.info("\n Solution 1 total cost {}", main.minAssemblyTime(stationTime, transferTime, entry, exit));
        log.info("\n Solution 2 total cost {}", main.minAssemblyTimeOptimized(stationTime, transferTime, entry, exit));
        log.info("\n Solution 2 total cost {}", main.minAssemblyTimeOptimizedOrig(stationTime, transferTime, entry, exit));
    }

    
    //Problem: Optimize production when some stations can run in parallel (like GM's flexible manufacturing systems).
    public int minAssemblyTime(int[][] stationTime, int[][] transferTime, int[] entry, int[] exit) {
        int n = stationTime[0].length;
        
        //create two dp lines
        int[] dpLine1 = new int[n];
        int[] dpLine2 = new int[n];
        // Entry time with entry 0 and entry 1 plus the first cell and second cell value fro the first station and second station
        dpLine1[0] = entry[0] + stationTime[0][0];
        dpLine2[0] = entry[1] + stationTime[1][0];

        for (int i = 1; i < n; i++) {
            dpLine1[i] = Math.min(
                    dpLine1[i - 1] + stationTime[0][i],
                    dpLine2[i - 1] + transferTime[1][i - 1] + stationTime[0][i]
            );

            dpLine2[i] = Math.min(
                    dpLine2[i - 1] + stationTime[1][i],
                    dpLine1[i - 1] + transferTime[0][i - 1] + stationTime[1][i]
            );
        }

        //return the min with the last dp lie value plus exit 0 or exit 1
        return Math.min(dpLine1[n - 1] + exit[0], dpLine2[n - 1] + exit[1]);
    }

    public int minAssemblyTimeNew(int[][] stationTime, int[][] transferTime, int[] entry, int[] exit) {
        int n = stationTime[0].length;
        
        //create two dp lines - duynamic programming lines
        int[] dpLine1 = new int[n];
        int[] dpLine2 = new int[n];
        // Entry time with entry 0 and entry 1 plus the first cell and second cell value fro the first station and second station
        dpLine1[0] = entry[0] + stationTime[0][0];
        dpLine2[0] = entry[1] + stationTime[1][0];        

        for (int i = 1; i < n; i++) {
            int priorIndex = i - 1;
            
            int station0i = stationTime[0][i];
            dpLine1[i] = Math.min(  dpLine1[priorIndex] +        0                    + station0i,
                                    dpLine2[priorIndex] + transferTime[1][priorIndex] + station0i);     
            
            int station1i = stationTime[1][i];
            dpLine2[i] = Math.min(  dpLine2[priorIndex] +        0                    + station1i,
                                    dpLine1[priorIndex] + transferTime[0][priorIndex] + station0i);     
            
            
            dpLine1[i] = Math.min(
                    dpLine1[i - 1] + stationTime[0][i],
                    dpLine2[i - 1] + transferTime[1][i - 1] + stationTime[0][i]
            );

            dpLine2[i] = Math.min(
                    dpLine2[i - 1] + stationTime[1][i],
                    dpLine1[i - 1] + transferTime[0][i - 1] + stationTime[1][i]
            );
        }

        //return the min with the last dp lie value plus exit 0 or exit 1
        return Math.min(dpLine1[n - 1] + exit[0], dpLine2[n - 1] + exit[1]);
    }

    /*
    Optimization Steps:
        Space Optimization: Reduce from O(n) to O(1) by tracking only the previous station's time:
    */
    public int minAssemblyTimeOptimized(int[][] stationTime, int[][] transferTime, int[] entry, int[] exit) {
        if (stationTime == null || transferTime == null || stationTime.length != transferTime.length || transferTime.length == 0
                || entry == null || exit == null || entry.length == 0 || exit.length == 0) {
            return 0;
        }

        
        int n = stationTime[0].length;
        
        // Entry time with entry 0 plus the first cell of the first station
        //                 entry 1 plus the first cell of the second station
        // initializing with celll 0
        int prevLine1 = entry[0] + stationTime[0][0];
        int prevLine2 = entry[1] + stationTime[1][0];
        
        //start with cell 1
        for (int i = 1; i < n; i++) {
            //find line 1 min of (1) the prevLine1 or (2) other prev (line 2) + other transfer (1) of the prev cell (i -1) + this station (0) of the current i
            int currLine1 = Math.min(prevLine1, prevLine2 + transferTime[1][i - 1]) + stationTime[0][i];
            //find line 2 min of (1) the prevLine2 or (2) other prev (line1) + this transfer (0) of the prev cell (i -1) + other station (1) of the current i
            int currLine2 = Math.min(prevLine2, prevLine1 + transferTime[0][i - 1]) + stationTime[1][i];
            
            //swap prev with curr
            prevLine1 = currLine1;
            prevLine2 = currLine2;
        }

        return Math.min(prevLine1 + exit[0], prevLine2 + exit[1]);
    }
    
    
    public int minAssemblyTimeOptimizedOrig(int[][] stationTime, int[][] transferTime, int[] entry, int[] exit) {
        if (stationTime == null || transferTime == null || stationTime.length != transferTime.length || transferTime.length == 0
                || entry == null || exit == null || entry.length == 0 || exit.length == 0) {
            return 0;
        }

        
        int n = stationTime[0].length;
        
        // Entry time with entry 0 plus the first cell of the first station
        //                 entry 1 plus the first cell of the second station
        // initializing with celll 0
        int prevLine1 = stationTime[0][0] + entry[0];
        int prevLine2 = stationTime[1][0] + entry[1];
        
        //start with cell 1
        for (int i = 1; i < n; i++) {
            //find line 1 min of (1) the prevLine1 or (2) other prev (line 2) + other transfer (1) of the prev cell (i -1) + this station (0) of the current i
            int currLine1 = stationTime[0][i] + Math.min(prevLine1, prevLine2 + transferTime[1][i - 1]);
            //find line 2 min of (1) the prevLine2 or (2) other prev (line1) + this transfer (0) of the prev cell (i -1) + other station (1) of the current i
            int currLine2 = stationTime[1][i] + Math.min(prevLine2, prevLine1 + transferTime[0][i - 1]);
            
            //swap prev with curr
            prevLine1 = currLine1;
            prevLine2 = currLine2;
        }

        prevLine1 += exit[0];
        prevLine2 += exit[1];

        return Math.min(prevLine1, prevLine2);
    }
}
