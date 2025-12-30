/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.jmeter;

import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UserControllerLoadTest {

    /*
    @Test
    public void userControllerShouldHandleLoad() throws Exception {
        JMeterTestRunner runner = new JMeterTestRunner();
        LoadTestResult result = runner.runTest("UserController-LoadTest.jmx");

        assertAll(
                () -> assertTrue(result.getErrorPercentage() < 1.0),
                () -> assertTrue(result.getAverageResponseTime() < 300),
                () -> assertTrue(result.getThroughput() > 100)
        );
    }
    // */
}
