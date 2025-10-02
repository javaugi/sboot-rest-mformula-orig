/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.payroll;

import com.spring5.empbilpayroll.Employee;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

@Service
@org.springframework.core.annotation.Order(14)
public class DroolsPayrollService implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger(DroolsPayrollService.class);

	@Override
	public void run(String... args) throws Exception {
		log.info("DroolsPayrollService ");
		main(args);
	}

	public static void main(String[] args) {
	}

	public void applyDrools(Employee employee) {
		// Kie = Knowledge Is Everything
		KieServices ks = KieServices.Factory.get();
		KieContainer kContainer = ks.getKieClasspathContainer();
		KieSession kSession = kContainer.newKieSession("payroll-rules");
		kSession.insert(employee); // Insert employee facts into the rule engine
		kSession.fireAllRules(); // Execute all matching rules
		kSession.dispose(); // Clean up (important!)
	}

}
