/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.integration;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Cookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

@ContextConfiguration
@SpringBootTest
@TestPropertySource(locations = "classpath:applicationtest.properties")
public abstract class ITBaseConfig {

    @Value("${com.ciminc.waitBetween}")
    public String waitBetween;

    @Value("${com.ciminc.testRootUrl}")
    public String testRootUrl;

    @Value("${com.ciminc.dashboardUrl}")
    public String dashboardUrl;

    @Value("${com.ciminc.activityUrl}")
    public String activityUrl;

    @Value("${com.ciminc.contextPath}")
    public String contextPath;

    @Value("${com.ciminc.testProfile}")
    public String testProfile;

    @Autowired
    Environment env;

    public ITBaseConfig() {
    }

    @Before
    public void setUp() throws Exception {
        waitBetween = env.getProperty("com.ciminc.waitBetween");
        testRootUrl = env.getProperty("com.ciminc.testRootUrl");
        dashboardUrl = env.getProperty("com.ciminc.dashboardUrl");
        activityUrl = env.getProperty("com.ciminc.activityUrl");
        contextPath = env.getProperty("com.ciminc.contextPath");
        testProfile = env.getProperty("com.ciminc.testProfile");
        System.setProperty("spring.profiles.active", testProfile);
        System.out.println("testProfile=" + testProfile + "-testRootUrl=" + testRootUrl);
    }

    @Test
    public void runTextBench() {
    }

    public void addCookie() {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.add(java.util.Calendar.DATE, 2);
        Cookie cookie
                = new Cookie.Builder("name", "value")
                        .domain("localhost")
                        .expiresOn(cal.getTime())
                        .isHttpOnly(true)
                        .isSecure(false)
                        .path(contextPath + dashboardUrl)
                        .build();
    }

    @After
    public void tearDown() throws Exception {
        wait1second();
        runCleanup();
    }

    public void wait1second() {
        if ("true".equals(waitBetween)) {
            try {
                // System.out.println("waitBetween=" + waitBetween);
                Thread.sleep(1000);
            } catch (Exception e) {
            }
        }
    }

    public void runCleanup() {
    }
}
