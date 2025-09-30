/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.gmcodility;

import java.util.List;
import lombok.extern.slf4j.Slf4j;

/*
Additional Preparation Tips
Review Vehicle Testing Concepts:
    Understand common automotive test types (battery, durability, emissions)
    Familiarize yourself with vehicle telemetry data structures

Practice Time Constraints:
    Codility tests are timed - practice solving similar problems within 30-45 minutes each

Focus on Clean Code:
    Proper naming conventions
    Consistent formatting
    Clear method separation

Prepare for Edge Cases:
    Think about null checks
    Consider boundary conditions
    Handle potential errors gracefully

Know Your Tools:
    Spring Boot annotations (@RestController, @Service, etc.)
    JPA/Hibernate features
    PostgreSQL specific functions (JSONB, window functions)
 */
@Slf4j
public class BatteryMain {

    public static void main(String[] args) {
        BatteryMain main = new BatteryMain();
        main.run();
    }

    private void run() {

        List<BatteryMeasurement> measurements
                = List.of(
                        BatteryMeasurement.builder().temperature(76).voltage(25).build(),
                        BatteryMeasurement.builder().temperature(57).voltage(21).build(),
                        BatteryMeasurement.builder().temperature(34).voltage(20).build(),
                        BatteryMeasurement.builder().temperature(46).voltage(29).build(),
                        BatteryMeasurement.builder().temperature(67).voltage(35).build(),
                        BatteryMeasurement.builder().temperature(52).voltage(31).build(),
                        BatteryMeasurement.builder().temperature(48).voltage(30).build(),
                        BatteryMeasurement.builder().temperature(40).voltage(26).build());

        log.info("Initial list {}", measurements);
        log.info(
                "Voltage average {}",
                measurements.stream().mapToDouble(BatteryMeasurement::getVoltage).average().orElse(0.0));
        log.info(
                "Voltage max {}",
                measurements.stream().mapToDouble(BatteryMeasurement::getVoltage).max().orElse(0.0));
        log.info(
                "Voltage min {}",
                measurements.stream().mapToDouble(BatteryMeasurement::getVoltage).min().orElse(0.0));
        log.info(
                "Voltage count {}",
                measurements.stream().mapToDouble(BatteryMeasurement::getVoltage).count());
        log.info(
                "Voltage count > 30 {}",
                measurements.stream()
                        .mapToDouble(BatteryMeasurement::getVoltage)
                        .filter(v -> v > 30)
                        .count());
        log.info(
                "Voltage count < 30 {}",
                measurements.stream()
                        .mapToDouble(BatteryMeasurement::getVoltage)
                        .filter(v -> v < 30)
                        .count());
        log.info(
                "Voltage boxed {}",
                measurements.stream().mapToDouble(BatteryMeasurement::getVoltage).boxed().toList());
    }
}

/*
GM Full Stack Developer Codility Test Simulation
Based on the job description, I'll create a simulated Codility test with three parts (API, SQL, and Java) that reflect the skills needed for this GM position working with vehicle test lab operations and validation systems.

Part 1: API Development (REST/Spring Boot)
Task: Create a vehicle test results API endpoint with filtering capabilities

Requirements:
    Build a Spring Boot endpoint that returns vehicle test results
    Support filtering by test type, date range, and pass/fail status
    Implement proper error handling
    Include unit tests

java
// VehicleTestResultController.java
@RestController
@RequestMapping("/api/vehicle-tests")
public class VehicleTestResultController {

    @Autowired
    private VehicleTestResultRepository testResultRepository;

    @GetMapping
    public ResponseEntity<List<VehicleTestResult>> getTestResults(
            @RequestParam(required = false) String testType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Boolean passed) {

        try {
            Specification<VehicleTestResult> spec = Specification.where(null);

            if (testType != null) {
                spec = spec.and((root, query, cb) -> cb.equal(root.get("testType"), testType));
            }

            if (startDate != null) {
                spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("testDate"), startDate));
            }

            if (endDate != null) {
                spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("testDate"), endDate));
            }

            if (passed != null) {
                spec = spec.and((root, query, cb) -> cb.equal(root.get("passed"), passed));
            }

            List<VehicleTestResult> results = testResultRepository.findAll(spec);
            return ResponseEntity.ok(results);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }
}

// VehicleTestResultRepository.java
public interface VehicleTestResultRepository extends JpaRepository<VehicleTestResult, Long>, JpaSpecificationExecutor<VehicleTestResult> {
}

// Unit Test
@SpringBootTest
@AutoConfigureMockMvc
public class VehicleTestResultControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VehicleTestResultRepository testResultRepository;

    @Test
    public void getTestResults_withFilters_shouldReturnFilteredResults() throws Exception {
        VehicleTestResult result = new VehicleTestResult(1L, "BATTERY", LocalDate.now(), true);

        when(testResultRepository.findAll(any(Specification.class)))
            .thenReturn(List.of(result));

        mockMvc.perform(get("/api/vehicle-tests")
                .param("testType", "BATTERY")
                .param("passed", "true"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].testType").value("BATTERY"))
            .andExpect(jsonPath("$[0].passed").value(true));
    }
}

Part 2: SQL (PostgreSQL)
Task: Write SQL queries for vehicle validation database

Scenario: You're working with a database that stores vehicle test results and component information.

sql
-- 1. Create tables for vehicle components and test results
CREATE TABLE vehicle_components (
    component_id SERIAL PRIMARY KEY,
    component_name VARCHAR(100) NOT NULL,
    component_type VARCHAR(50) NOT NULL,
    manufacturer VARCHAR(100),
    specifications JSONB
);

CREATE TABLE test_results (
    test_id SERIAL PRIMARY KEY,
    component_id INTEGER REFERENCES vehicle_components(component_id),
    test_date TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    test_type VARCHAR(50) NOT NULL,
    passed BOOLEAN NOT NULL,
    test_parameters JSONB NOT NULL,
    test_metrics JSONB NOT NULL
);

-- 2. Find the failure rate by component type for the last 30 days
SELECT
    vc.component_type,
    COUNT(*) AS total_tests,
    SUM(CASE WHEN tr.passed = FALSE THEN 1 ELSE 0 END) AS failed_tests,
    ROUND(100.0 * SUM(CASE WHEN tr.passed = FALSE THEN 1 ELSE 0 END) / COUNT(*), 2) AS failure_rate
FROM test_results tr
JOIN vehicle_components vc ON tr.component_id = vc.component_id
WHERE tr.test_date >= NOW() - INTERVAL '30 days'
GROUP BY vc.component_type
ORDER BY failure_rate DESC;

-- 3. Create a materialized view for battery component test statistics
CREATE MATERIALIZED VIEW battery_test_stats AS
SELECT
    vc.component_id,
    vc.manufacturer,
    COUNT(*) AS test_count,
    AVG((tr.test_metrics->>'capacity')::numeric) AS avg_capacity,
    MIN((tr.test_metrics->>'temperature')::numeric) AS min_temp,
    MAX((tr.test_metrics->>'temperature')::numeric) AS max_temp
FROM test_results tr
JOIN vehicle_components vc ON tr.component_id = vc.component_id
WHERE vc.component_type = 'BATTERY'
GROUP BY vc.component_id, vc.manufacturer;

-- 4. Write a query to find components that failed tests with similar parameters
SELECT
    vc.component_id,
    vc.component_name,
    tr.test_type,
    tr.test_parameters->>'load' AS test_load,
    COUNT(*) AS failure_count
FROM test_results tr
JOIN vehicle_components vc ON tr.component_id = vc.component_id
WHERE tr.passed = FALSE
GROUP BY vc.component_id, vc.component_name, tr.test_type, tr.test_parameters->>'load'
HAVING COUNT(*) > 3
ORDER BY failure_count DESC;

Part 3: Java Procedure (Data Processing)
Task: Implement a battery test validation processor

Requirements:
    Process a stream of battery test measurements
    Calculate statistics and validate against thresholds
    Identify anomalies
    Write results to database

java
// BatteryTestProcessor.java
@Service
public class BatteryTestProcessor {

    @Autowired
    private BatteryTestRepository batteryTestRepository;

    private static final double MIN_VOLTAGE = 2.7;
    private static final double MAX_VOLTAGE = 4.2;
    private static final double MAX_TEMP = 45.0;

    public BatteryTestResult processTestData(BatteryTestData testData) {
        // Calculate statistics
        double avgVoltage = calculateAverageVoltage(testData.getMeasurements());
        double maxTemp = calculateMaxTemperature(testData.getMeasurements());
        double capacity = calculateCapacity(testData);

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
            anomalies.add(String.format("Low capacity: %.2fAh (rated %.2fAh)",
                capacity, testData.getRatedCapacity()));
        }

        // Create and save result
        BatteryTestResult result = new BatteryTestResult();
        result.setTestId(testData.getTestId());
        result.setTestDate(LocalDateTime.now());
        result.setPassed(passed);
        result.setAnomalies(anomalies);
        result.setMetrics(Map.of(
            "averageVoltage", avgVoltage,
            "maxTemperature", maxTemp,
            "measuredCapacity", capacity
        ));

        return batteryTestRepository.save(result);
    }

    private double calculateAverageVoltage(List<BatteryMeasurement> measurements) {
        return measurements.stream()
            .mapToDouble(BatteryMeasurement::getVoltage)
            .average()
            .orElse(0.0);
    }

    private double calculateMaxTemperature(List<BatteryMeasurement> measurements) {
        return measurements.stream()
            .mapToDouble(BatteryMeasurement::getTemperature)
            .max()
            .orElse(0.0);
    }

    private double calculateCapacity(BatteryTestData testData) {
        // Simplified capacity calculation
        double dischargeCurrent = testData.getDischargeCurrent();
        long dischargeTime = testData.getDischargeTimeSeconds();
        return (dischargeCurrent * dischargeTime) / 3600.0; // Convert to Ah
    }
}

// Unit Test
public class BatteryTestProcessorTest {

    private BatteryTestProcessor processor;
    private BatteryTestRepository repository;

    @BeforeEach
    public void setUp() {
        repository = mock(BatteryTestRepository.class);
        processor = new BatteryTestProcessor(repository);
    }

    @Test
    public void processTestData_withValidData_shouldPass() {
        BatteryTestData testData = new BatteryTestData(
            "test123",
            5.0, // rated capacity
            1.0, // discharge current
            18000, // discharge time (5 hours)
            List.of(
                new BatteryMeasurement(3.7, 25.0),
                new BatteryMeasurement(3.6, 26.0)
            )
        );

        BatteryTestResult result = processor.processTestData(testData);

        assertTrue(result.isPassed());
        assertEquals(0, result.getAnomalies().size());
        assertEquals(5.0, result.getMetrics().get("measuredCapacity"));
    }

    @Test
    public void processTestData_withHighTemp_shouldFail() {
        BatteryTestData testData = new BatteryTestData(
            "test456",
            5.0,
            1.0,
            18000,
            List.of(
                new BatteryMeasurement(3.7, 50.0) // High temp
            )
        );

        BatteryTestResult result = processor.processTestData(testData);

        assertFalse(result.isPassed());
        assertEquals(1, result.getAnomalies().size());
        assertTrue(result.getAnomalies().get(0).contains("Temperature exceeded"));
    }
}


Additional Preparation Tips
Review Vehicle Testing Concepts:
    Understand common automotive test types (battery, durability, emissions)
    Familiarize yourself with vehicle telemetry data structures

Practice Time Constraints:
    Codility tests are timed - practice solving similar problems within 30-45 minutes each

Focus on Clean Code:
    Proper naming conventions
    Consistent formatting
    Clear method separation

Prepare for Edge Cases:
    Think about null checks
    Consider boundary conditions
    Handle potential errors gracefully

Know Your Tools:
    Spring Boot annotations (@RestController, @Service, etc.)
    JPA/Hibernate features
    PostgreSQL specific functions (JSONB, window functions)

Good luck on your Codility test! Remember to read each problem carefully and start with the core requirements before adding bonus features if time permits.
 */
