/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.payroll;

import com.spring5.empbilpayroll.Employee;

/**
 * @author javaugi
 */
public class EUPayrollRule implements PayrollRuleStrategy {

	@Override
	public double calculateOvertime(double hoursWorked, Employee employee) {
		return (hoursWorked > 40) ? (hoursWorked - 40) * 1.5 : 0;
	}

	@Override
	public double calculateTax(Employee employee) {
		return employee.getSalary();
	}

}
