/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.payroll;

import com.spring5.empbilpayroll.Employee;
import com.spring5.empbilpayroll.PayrollData;

/**
 *
 * @author javaugi
 */
public abstract  class PayrollChainOfRespValidator {
    private PayrollChainOfRespValidator next;

    public PayrollChainOfRespValidator linkWith(PayrollChainOfRespValidator next) {
        this.next = next;
        return next;
    }

    public abstract boolean validate(Employee employee, PayrollData data) throws Exception;

    protected boolean validateNext(Employee employee, PayrollData data) throws Exception {
        return (next == null) || next.validate(employee, data);
    }    
}
