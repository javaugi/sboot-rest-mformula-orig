/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.payroll;

/**
 * @author javaugi
 */
public abstract class BillingServiceDecorator implements BillingService {

    protected BillingService wrapped;

    public BillingServiceDecorator(BillingService service) {
        this.wrapped = service;
    }
}
