/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.jmeter;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.config.gui.ArgumentsPanel;
import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.control.gui.LoopControlPanel;
import org.apache.jmeter.control.gui.TestPlanGui;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.protocol.http.control.Header;
import org.apache.jmeter.protocol.http.control.HeaderManager;
import org.apache.jmeter.protocol.http.gui.HeaderPanel;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
import org.apache.jmeter.protocol.http.util.HTTPConstants;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.reporters.Summariser;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.threads.gui.ThreadGroupGui;
import org.apache.jmeter.threads.ThreadGroup;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;
//import org.junit.jupiter.params.provider.Arguments;
//import org.junit.platform.launcher.TestPlan;

public class ProgrammaticJMeterTest {

    //@Test
    public void createAndRunJMeterTestProgrammatically() {
        // JMeter Engine
        StandardJMeterEngine jmeter = new StandardJMeterEngine();

        // JMeter initialization
        JMeterUtils.initLocale();

        // Test Plan
        TestPlan testPlan = new TestPlan("User Controller Load Test");
        testPlan.setProperty(TestElement.TEST_CLASS, TestPlan.class.getName());
        testPlan.setProperty(TestElement.GUI_CLASS, TestPlanGui.class.getName());
        testPlan.setUserDefinedVariables((Arguments) new ArgumentsPanel().createTestElement());

        // Thread Group
        ThreadGroup threadGroup = new ThreadGroup();
        threadGroup.setName("User Controller Thread Group");
        threadGroup.setNumThreads(10);
        threadGroup.setRampUp(5);
        threadGroup.setSamplerController(createLoopController());
        threadGroup.setProperty(TestElement.TEST_CLASS, ThreadGroup.class.getName());
        threadGroup.setProperty(TestElement.GUI_CLASS, ThreadGroupGui.class.getName());
        // */

        // HTTP Sampler - GET All Users
        HTTPSamplerProxy getUsersSampler = new HTTPSamplerProxy();
        getUsersSampler.setDomain("localhost");
        getUsersSampler.setPort(8080);
        getUsersSampler.setPath("/api/users");
        getUsersSampler.setMethod(HTTPConstants.GET);
        getUsersSampler.setName("GET All Users");
        getUsersSampler.setProperty(TestElement.TEST_CLASS, HTTPSamplerProxy.class.getName());
        getUsersSampler.setProperty(TestElement.GUI_CLASS, "TestBeanGUI");

        // HTTP Sampler - POST Create User
        HTTPSamplerProxy postUserSampler = new HTTPSamplerProxy();
        postUserSampler.setDomain("localhost");
        postUserSampler.setPort(8080);
        postUserSampler.setPath("/api/users");
        postUserSampler.setMethod(HTTPConstants.POST);
        postUserSampler.setName("POST Create User");

        // Add JSON body
        postUserSampler.addNonEncodedArgument("",
                "{\"name\":\"Test User\",\"email\":\"test@example.com\",\"age\":30}", "");
        postUserSampler.setPostBodyRaw(true);

        // Header Manager
        HeaderManager headerManager = new HeaderManager();
        headerManager.add(new Header("Content-Type", "application/json"));
        headerManager.setName("HTTP Header Manager");
        headerManager.setProperty(TestElement.TEST_CLASS, HeaderManager.class.getName());
        headerManager.setProperty(TestElement.GUI_CLASS, HeaderPanel.class.getName());

        // Build test plan tree
        HashTree testPlanTree = new HashTree();
        HashTree threadGroupHashTree = testPlanTree.add(testPlan, threadGroup);
        threadGroupHashTree.add(getUsersSampler);
        threadGroupHashTree.add(postUserSampler);
        threadGroupHashTree.add(headerManager);

        // Add listener
        Summariser summariser = null;
        String summariserName = JMeterUtils.getPropDefault("summariser.name", "summary");
        if (summariserName.length() > 0) {
            summariser = new Summariser(summariserName);
        }

        ResultCollector logger = new ResultCollector(summariser);
        logger.setFilename("target/jmeter-results.jtl");
        testPlanTree.add(testPlanTree.getArray()[0], logger);

        // Run JMeter Test
        jmeter.configure(testPlanTree);
        jmeter.run();
    }

    private LoopController createLoopController() {
        LoopController loopController = new LoopController();
        loopController.setLoops(5);
        loopController.setFirst(true);
        loopController.setProperty(TestElement.TEST_CLASS, LoopController.class.getName());
        loopController.setProperty(TestElement.GUI_CLASS, LoopControlPanel.class.getName());
        loopController.initialize();
        return loopController;
    }
}
