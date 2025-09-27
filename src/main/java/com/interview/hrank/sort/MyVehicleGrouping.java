/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.interview.hrank.sort;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
//import java.util.stream.*;

@Slf4j
public class MyVehicleGrouping {

    static MyVehicleGrouping main = new MyVehicleGrouping();

    public static void main(String[] args) {
        List<Vehicle> list = main.getVehicles();
        main.countMakeModels(list);
    }

    public static Integer[] countMakeModels(List<Vehicle> list) {

        Integer[] rtn = new Integer[2];

        Map<String, List<Vehicle>> gList
            = list.stream().parallel()
                .sorted(Comparator.comparing(Vehicle::getMake)
                .thenComparing(Vehicle::getModel)
                ).collect(Collectors.groupingBy(Vehicle::getMake));

        final AtomicInteger makeCnt = new AtomicInteger();
        final AtomicInteger modelCnt = new AtomicInteger();
        gList.keySet().forEach(e -> {
            makeCnt.incrementAndGet();
            Printer.print("Make=" + e);
            gList.get(e).stream().forEach(v -> {
                modelCnt.incrementAndGet();
                Printer.print("     Model=" + v.getModel());
            });
        });

        rtn[0] = makeCnt.get();
        rtn[1] = modelCnt.get();
        Printer.print("Final Return [Make Count, Model Count]=" + Arrays.toString(rtn));
        return rtn;
    }

    public static final GenericPrint Printer = e -> log.info("" + e);

    public interface GenericPrint<T> {

        void print(T t);
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

        String make;
        String model;
        double cost;
        int rating;

    }
}
