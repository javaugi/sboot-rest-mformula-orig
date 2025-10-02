/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.gmcodility;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
@Entity
@Table(name = "BATTERY_TEST_RESULTS")
public class BatteryTestResult {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private long id;

	private String testId;

	private String vehicleId;

	private LocalDateTime testDate;

	private boolean passed;

	// String.join(",", anomalies), or
	// anomalies.stream().collect(Collectors.joining(","));
	private String anomalies;

	private String metrics;

}
