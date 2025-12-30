/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.interview.hrank.sort;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import lombok.Builder;
import lombok.Data;

/**
 *
 * @author javau
 */
public class WeatherTempSort {
    
    public static void main(String[] args) {
        sortPrint();
    }

    private static void sortPrint() {
        List<Weather> list = List.of(
                Weather.builder().date(new Date()).temperature(32.25).build(),
                Weather.builder().date(new Date()).temperature(33.25).build(),
                Weather.builder().date(new Date()).temperature(34.25).build(),
                Weather.builder().date(new Date()).temperature(35.25).build(),
                Weather.builder().date(new Date()).temperature(36.25).build(),
                Weather.builder().date(new Date()).temperature(37.25).build()
        );
        
        list.stream().sorted(Comparator.comparing(Weather::getTemperature))
                .forEach(p -> {
            System.out.println(p);
        });

    }

    @Data
    @Builder
    static class Weather {

        Date date;
        double temperature;
    }
}
