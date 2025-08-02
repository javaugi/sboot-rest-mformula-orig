/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.payroll;

import com.spring5.empbilpayroll.Employee;
import java.math.BigDecimal;

/**
 *
 * @author javaugi
 */
public interface TaxCalculator {
    BigDecimal calculate(Employee employee, PayrollContext context);    
}
