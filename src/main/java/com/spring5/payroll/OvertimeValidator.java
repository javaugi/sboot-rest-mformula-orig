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
public class OvertimeValidator extends PayrollChainOfRespValidator {

	@Override
	public boolean validate(Employee employee, PayrollData data) throws Exception {
		/*
		 * if (data.getHoursWorked() > employee.getContract().getMaxWeeklyHours()) { throw
		 * new PayrollValidationException( "Overtime exceeded for employee " +
		 * employee.getId() + ": " + data.getHoursWorked() + " hours" ); }
		 */
		if (data.getHoursWorked() > 48) {
			throw new PayrollValidationException("EU max 48 hours/week");
		}
		return validateNext(employee, data);
	}

}
