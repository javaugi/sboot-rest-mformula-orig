/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.interview.hrank.sort;

import lombok.Builder;
import lombok.Data;
import java.util.List;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author javau
 */
public class VehicleGroupSort {

    public static void main(String[] args) {
        List<Vehicle> list = getVehicles();
        countMakeModels(list);
    }

    public static Integer[] countMakeModels(List<Vehicle> list) {
        Integer[] rtn = new Integer[2];

        Map<String, List<Vehicle>> grouped
            = list.stream()
                .sorted(Comparator.comparing(Vehicle::getMake)
                .thenComparing(Vehicle::getCost)
                .thenComparing(Vehicle::getRating).reversed()
                .thenComparing(Vehicle::getModel)
                ).collect(Collectors.groupingBy(Vehicle::getMake));
        List<String> keys = grouped.keySet().stream().sorted(Comparator.reverseOrder()).toList();
        for (String key : keys) {
            System.out.println("Make=" + key);
            for (Vehicle v : grouped.get(key)) {
                System.out.println("    " + v);
            }
        }

        return rtn;
    }

    static void run2() {
        ExecutorService executorService = Executors.newFixedThreadPool(20);
        List<Vehicle> list = getVehicles();

        List<CompletableFuture<Vehicle>> futures = list.stream()
            .map(v -> CompletableFuture.supplyAsync(()
            -> process(v), executorService))
            .collect(Collectors.toList());


    }

    private static Vehicle process(Vehicle v) {
        return v;
    }

    public static List<Vehicle> getVehicles() {
        return List.of(Vehicle.builder().make("Ford").model("F1").build(),
            Vehicle.builder().make("Ford").model("F2").build(),
            Vehicle.builder().make("Ford").model("F3").build(),
            Vehicle.builder().make("Ford").model("F4").build(),
            Vehicle.builder().make("GM").model("G1").build(),
            Vehicle.builder().make("GM").model("G2").build(),
            Vehicle.builder().make("GM").model("G3").build()
        );
    }

    @Data
    @Builder(toBuilder = true)
    public static class Vehicle {

        Long id;
        String make;
        String model;
        Double cost;
        Integer rating;

        @Override
        public String toString() {
            return "    mkae=" + make + "   :model=" + model;
        }
    }

}
