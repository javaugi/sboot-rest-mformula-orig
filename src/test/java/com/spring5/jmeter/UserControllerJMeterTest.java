/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.jmeter;

import java.io.File;
import java.nio.file.Path;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.reporters.Summariser;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;
import org.junit.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.io.TempDir;

public class UserControllerJMeterTest {

    @TempDir
    Path tempDir;

    //@Test
    public void testUserControllerLoad() throws Exception {
        // Setup JMeter home
        String jmeterHome = System.getenv("JMETER_HOME");
        if (jmeterHome == null) {
            jmeterHome = "/opt/apache-jmeter-5.6.2"; // Adjust path
        }

        JMeterUtils.setJMeterHome(jmeterHome);
        JMeterUtils.loadJMeterProperties(jmeterHome + "/bin/jmeter.properties");
        JMeterUtils.initLocale();

        // Load existing JMX test plan
        File jmxFile = new File("src/test/jmeter/UserController-LoadTest.jmx");
        HashTree testPlanTree = SaveService.loadTree(jmxFile);

        // Configure results
        Summariser summariser = null;
        String summariserName = JMeterUtils.getPropDefault("summariser.name", "summary");
        if (summariserName.length() > 0) {
            summariser = new Summariser(summariserName);
        }

        File resultsFile = new File(tempDir.toFile(), "results.jtl");
        ResultCollector resultCollector = new ResultCollector(summariser);
        resultCollector.setFilename(resultsFile.getAbsolutePath());
        testPlanTree.add(testPlanTree.getArray()[0], resultCollector);

        // Run test
        StandardJMeterEngine jmeter = new StandardJMeterEngine();
        jmeter.configure(testPlanTree);
        jmeter.run();

        // Wait for test to complete
        Thread.sleep(30000); // Adjust based on test duration

        // Analyze results
        JMeterResultAnalyzer analyzer = new JMeterResultAnalyzer();
        LoadTestResult result = analyzer.analyzeResults(resultsFile);

        // Assertions
        assertTrue(result.getErrorPercentage() < 1.0,
                "Error percentage should be less than 1%");
        assertTrue(result.getAverageResponseTime() < 1000,
                "Average response time should be less than 1000ms");
        assertTrue(result.getThroughput() > 10,
                "Throughput should be greater than 10 req/sec");
    }
}
