/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.payroll;

// import com.spring5.jpapagination.Region;
import com.spring5.empbilpayroll.Region;
import static com.spring5.empbilpayroll.Region.EU;

// import static java.util.Locale.US;
/**
 * @author javaugi
 */
public class PayrollRuleFactory {

	public static PayrollRuleStrategy getRule(Region region) {
		switch (region) {
			case US -> {
				return new USPayrollRule();
			}
			case EU -> {
				return new EUPayrollRule();
			}
			default -> throw new IllegalArgumentException("Unsupported region");
		}
	}

}
