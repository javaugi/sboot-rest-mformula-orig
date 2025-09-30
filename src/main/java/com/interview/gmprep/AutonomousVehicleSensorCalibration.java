/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.interview.gmprep;

import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AutonomousVehicleSensorCalibration {

    private static final AutonomousVehicleSensorCalibration m
            = new AutonomousVehicleSensorCalibration();

    public static void main(String[] args) {
        int[][] tasks = {
            {4, 7, 8, 9, 6, 8, 10, 8},
            {
                5, 7,}
        };
        // index 0 as task number and index 1 as task cost
        int[] tt = {2, 3, 5, 6};

        log.info("minCalibrationTime {}", m.minCalibrationTime(tasks, tt));

        int[][] tasks2 = {{4, 7, 8}, {10, 8, 9}, {6, 4, 8}, {10, 6, 8}, {5, 8, 7}, {5, 12, 13}};
        int[] tt2 = {2, 9, 3, 5, 6};
        log.info("2 minCalibrationTime {}", m.minCalibrationTime(tasks2, tt2));
    }

    /*
  Problem: Schedule sensor calibration tasks to minimize downtime during production.
  Optimization:
      Greedy Assignment: Pair longest tasks with fastest technicians:
     */
    public int minCalibrationTime(int[][] tasks, int[] technicianTimes) {

        log.info(
                "minCalibrationTime before sort \n\t *** technicianTimes={} \t *** tasks={}",
                Arrays.toString(technicianTimes),
                Arrays.deepToString(tasks));
        Arrays.sort(tasks, (a, b) -> (b[1] - a[1])); // Sort by descending duration
        Arrays.sort(
                technicianTimes); // Ascending order for Greedy Assignment: Pair longest tasks with fastest
        // technicians:
        log.info(
                "minCalibrationTime after sort \n\t *** technicianTimes={} \t *** tasks={}",
                Arrays.toString(technicianTimes),
                Arrays.deepToString(tasks));

        int maxTime = 0;
        for (int i = 0; i < tasks.length; i++) {
            log.info("looping \t i={}, \t {}", i, Arrays.toString(tasks[i]));

            int tt = technicianTimes[i % technicianTimes.length];

            int taskTime = tasks[i][1] + tt;

            maxTime = Math.max(maxTime, taskTime);

            log.info(
                    "looping \t i={}, \t tt={}, \t taskTime={}, \t maxTime={} \t tasks[i][1]={}",
                    i,
                    tt,
                    taskTime,
                    maxTime,
                    tasks[i][1]);
        }
        return maxTime;
    }
}
