/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.empbilpayroll;

/**
 * @author javaugi
 */
public class Customer {

    private String name;
    private String type;
    private double totalPurchase;
    private double discount;

    public Customer(String name, String type, double totalPurchase) {
        this.name = name;
        this.type = type;
        this.totalPurchase = totalPurchase;
        this.discount = 0.0;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public double getTotalPurchase() {
        return totalPurchase;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }
}
