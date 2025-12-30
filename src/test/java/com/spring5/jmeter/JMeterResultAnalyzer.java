/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.jmeter;

//import com.helger.commons.csv.CSVParser;
//import com.helger.commons.csv.CSVReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.junit.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JMeterResultAnalyzer {

    public LoadTestResult analyzeResults(String resultsFile) throws Exception {
        return this.analyzeResults(new File(resultsFile));
    }

    public LoadTestResult analyzeResults(File resultsFile) throws Exception {
        LoadTestResult result = new LoadTestResult();
        List<Long> responseTimes = new ArrayList<>();
        long totalRequests = 0;
        long failedRequests = 0;

        try (Reader reader = new FileReader(resultsFile); CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                .withFirstRecordAsHeader()
                .withIgnoreHeaderCase()
                .withTrim())) {

            for (CSVRecord record : csvParser) {
                totalRequests++;

                String success = record.get("success");
                if ("false".equals(success)) {
                    failedRequests++;
                }

                try {
                    long responseTime = Long.parseLong(record.get("elapsed"));
                    responseTimes.add(responseTime);
                } catch (NumberFormatException e) {
                    // Skip invalid response time
                }
            }
        }

        // Calculate metrics
        double avgResponseTime = responseTimes.stream()
                .mapToLong(Long::longValue)
                .average()
                .orElse(0.0);

        double errorPercentage = (double) failedRequests / totalRequests * 100;

        // Estimate throughput (simplified)
        double throughput = totalRequests / 60.0; // Assuming 1-minute test

        result.setAverageResponseTime(avgResponseTime);
        result.setErrorPercentage(errorPercentage);
        result.setThroughput(throughput);
        result.setTotalRequests(totalRequests);
        result.setFailedRequests(failedRequests);

        return result;
    }

    @Test
    public void testJMeterResults() throws Exception {
        JMeterResultAnalyzer analyzer = new JMeterResultAnalyzer();
        LoadTestResult result = analyzer.analyzeResults("target/jmeter-results.jtl");

        // Performance assertions
        assertTrue(result.getErrorPercentage() < 1.0,
                "Error rate should be below 1%");

        assertTrue(result.getAverageResponseTime() < 500,
                "Average response time should be below 500ms");

        assertTrue(result.getThroughput() > 50,
                "Throughput should be above 50 req/sec");

        assertEquals(0, result.getFailedRequests(),
                "There should be no failed requests");
    }
}
