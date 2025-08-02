/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.payroll;

import com.spring5.empbilpayroll.Employee;

public abstract class PayrollDecorator implements PayrollRuleStrategy {
    protected PayrollRuleStrategy wrappedRule;

    public PayrollDecorator(PayrollRuleStrategy rule) {
        this.wrappedRule = rule;
    }
    
    @Override
    public double calculateTax(Employee employee) {
        return wrappedRule.calculateTax(employee);
    }  
}
