/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.payroll;

import com.spring5.empbilpayroll.Employee;
import com.spring5.empbilpayroll.PayrollData;
import com.spring5.validatorex.PayrollValidationException;

/**
 * @author javaugi
 */
public class TaxComplianceValidator extends PayrollChainOfRespValidator {

	@Override
	public boolean validate(Employee employee, PayrollData data) throws Exception {
		if (data.getTaxDeductions() == null || data.getTaxDeductions().isEmpty()) {
			throw new PayrollValidationException("Tax deductions missing for employee " + employee.getId());
		}
		return validateNext(employee, data);
	}

}
