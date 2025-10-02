/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.gmcodility;

// BatteryTestProcessor.java
import com.spring5.utils.CommonUtils;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BatteryTestProcessor {

	@Autowired
	private final BatteryTestRepository batteryTestRepository;

	public BatteryTestProcessor(BatteryTestRepository batteryTestRepository) {
		this.batteryTestRepository = batteryTestRepository;
	}

	private static final double MIN_VOLTAGE = 2.7;

	private static final double MAX_VOLTAGE = 4.2;

	private static final double MAX_TEMP = 45.0;

	public BatteryTestResult processTestData(BatteryTestData testData) {
		// Calculate statistics
		double avgVoltage = calculateAverageVoltage(testData.getMeasurements());
		double maxTemp = calculateMaxTemperature(testData.getMeasurements());
		double capacity = calculateCapacity(testData);
		Map<String, Long> testFailed = new HashMap();

		// Validate against thresholds
		boolean passed = true;
		List<String> anomalies = new ArrayList<>();

		if (avgVoltage < MIN_VOLTAGE || avgVoltage > MAX_VOLTAGE) {
			passed = false;
			anomalies.add(String.format("Voltage out of range: %.2fV", avgVoltage));
		}

		if (maxTemp > MAX_TEMP) {
			passed = false;
			anomalies.add(String.format("Temperature exceeded: %.1f°C", maxTemp));
		}

		if (capacity < testData.getRatedCapacity() * 0.8) {
			passed = false;
			anomalies.add(String.format("Low capacity: %.2fAh (rated %.2fAh)", capacity, testData.getRatedCapacity()));
		}

		// Create and save result
		BatteryTestResult result = BatteryTestResult.builder().build();
		result.setTestId(testData.getTestId());
		result.setTestDate(LocalDateTime.now());
		result.setPassed(passed);
		result.setAnomalies(CommonUtils.listToString(anomalies));
		result.setMetrics(CommonUtils.convertMapToJson(Map.of("averageVoltage", avgVoltage, "maxTemperature", maxTemp,
				"measuredCapacity", capacity, "failedTests    ", testFailed)));

		return batteryTestRepository.save(result);
	}

	private double calculateAverageVoltage(List<BatteryMeasurement> measurements) {
		return measurements.stream().mapToDouble(BatteryMeasurement::getVoltage).average().orElse(0.0);
	}

	private double calculateMaxTemperature(List<BatteryMeasurement> measurements) {
		return measurements.stream().mapToDouble(BatteryMeasurement::getTemperature).max().orElse(0.0);
	}

	private double calculateCapacity(BatteryTestData testData) {
		// Simplified capacity calculation
		double dischargeCurrent = testData.getDischargeCurrent();
		long dischargeTime = testData.getDischargeTimeSeconds();
		return (dischargeCurrent * dischargeTime) / 3600.0; // Convert to Ah
	}

	public Map<String, Long> countFailedTests(List<BatteryTestResult> tests) {
		return tests.stream()
			.filter(tr -> !tr.isPassed())
			.collect(Collectors.groupingBy(tr -> tr.getTestId(), Collectors.counting()));
	}

	public String getMostTestedVehicleLast7Days(List<BatteryTestResult> tests) {
		LocalDateTime oneWeekAgo = LocalDateTime.now().minusDays(7);
		return tests.stream()
			.filter(t -> t.getTestDate().isAfter(oneWeekAgo))
			.collect(Collectors.groupingBy(t -> t.getVehicleId(), Collectors.counting()))
			.entrySet()
			.stream()
			.max(Map.Entry.comparingByValue())
			.map(Map.Entry::getKey)
			.orElse(null);
	}

}
