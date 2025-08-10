/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.payroll;

import com.spring5.validatorex.PayrollValidationException;
import com.spring5.empbilpayroll.Employee;
import com.spring5.empbilpayroll.PayrollData;

/**
 *
 * @author javaugi
 */
public class EUWorkingHoursValidator extends PayrollChainOfRespValidator {
    
    @Override
    public boolean validate(Employee employee, PayrollData data) throws Exception {
        if (data.getHoursWorked() > 48) {
            throw new PayrollValidationException("EU max 48 hours/week");
        }
        return validateNext(employee, data);
    }    
}
