/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.jmeter;

import org.junit.jupiter.api.Test;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class JMeterCommandLineTest {

    //@Test
    public void runJMeterTestViaCommandLine() throws Exception {
        String jmeterHome = System.getenv("JMETER_HOME");
        String jmeterPath = jmeterHome + "/bin/jmeter";
        String testPlan = "src/test/jmeter/UserController-LoadTest.jmx";
        String resultsFile = "target/jmeter-results.jtl";

        ProcessBuilder processBuilder = new ProcessBuilder(
                jmeterPath,
                "-n", // non-GUI mode
                "-t", testPlan,
                "-l", resultsFile,
                "-e", // generate report at end
                "-o", "target/jmeter-report"
        );

        Process process = processBuilder.start();

        // Read output
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));

        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }

        int exitCode = process.waitFor();
        assertEquals(0, exitCode, "JMeter should complete successfully");

        // Now analyze results
        JMeterResultAnalyzer analyzer = new JMeterResultAnalyzer();
        LoadTestResult result = analyzer.analyzeResults(resultsFile);

        // Your assertions here
        assertTrue(result.getErrorPercentage() < 5.0);
    }
}
