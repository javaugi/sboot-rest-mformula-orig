/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.payroll;

import com.spring5.empbilpayroll.Employee;
import com.spring5.empbilpayroll.PayrollData;

/**
 * @author javaugi
 */
public class MinimumWageValidator extends PayrollChainOfRespValidator {

    @Override
    public boolean validate(Employee employee, PayrollData data) throws Exception {
        /*
    BigDecimal minWage = employee.getRegion().getMinimumWage();
    if (data.getHourlyRate().compareTo(minWage) < 0) {
        throw new PayrollValidationException(
            "Hourly rate (" + data.getHourlyRate() + ") " +
            "below minimum wage (" + minWage + ") for region " +
            employee.getRegion()
        );
    }
    // */
        return validateNext(employee, data);
    }
}
