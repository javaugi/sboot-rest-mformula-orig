/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.payroll;

import com.spring5.empbilpayroll.Customer;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;

@org.springframework.core.annotation.Order(13)
public class DroolsCustomerService implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger(DroolsCustomerService.class);

	@Override
	public void run(String... args) throws Exception {
		log.info("DroolsCustomerService ");
		main(args);
	}

	public static void main(String[] args) {
		Customer customer = new Customer("Alice", "VIP", 1500.0);

		applyDroolsToCustomer(customer);
	}

	private static void applyDroolsToCustomer(Customer customer) {
		if (customer == null) {
			customer = new Customer("Alice", "VIP", 1500.0);
		}
		KieServices ks = KieServices.Factory.get();
		KieContainer kc = ks.getKieClasspathContainer();
		KieSession ksession = kc.newKieSession("ksession-rules");

		ksession.insert(customer);
		ksession.fireAllRules();
		ksession.dispose();

		System.out.println("Final discount: " + customer.getDiscount());
	}

}
