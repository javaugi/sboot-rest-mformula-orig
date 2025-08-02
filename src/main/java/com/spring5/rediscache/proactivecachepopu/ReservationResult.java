/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.rediscache.proactivecachepopu;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class ReservationResult {
    static ReservationResult failure(String vehicle_not_available) {
        return ReservationResult.builder().failure(vehicle_not_available).build();
    }

    static ReservationResult success(String eventId) {
        return ReservationResult.builder().failure(eventId).build();
    }
    String success;
    String failure;
}
