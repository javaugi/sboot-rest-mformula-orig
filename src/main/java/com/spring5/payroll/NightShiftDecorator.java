/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.payroll;

import com.spring5.empbilpayroll.Employee;

public class NightShiftDecorator extends PayrollDecorator {
    
    public NightShiftDecorator(PayrollRuleStrategy rule) {
        super(rule);
    }
    
    @Override
    public double calculateOvertime(double hoursWorked, Employee employee) {
        return wrappedRule.calculateOvertime(hoursWorked, employee) 
               + (employee.isNightShift() ? hoursWorked * 0.2 : 0);
    }
    
    @Override
    public double calculateTax(Employee employee) {
        return 0d;
    }  
}