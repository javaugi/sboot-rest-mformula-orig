/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.gmcodility;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
@Entity
@Table(name = "BATTERY_TEST_DATA")
public class BatteryTestData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private long id;

    private String testId;
    private String vehicleId;
    private double dischargeCurrent;
    private long dischargeTimeSeconds;
    private double ratedCapacity;

    @OneToMany(
            mappedBy = "batteryTestData",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            targetEntity = BatteryMeasurement.class)
    private List<BatteryMeasurement> measurements;
}
