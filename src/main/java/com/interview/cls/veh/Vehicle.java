/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.interview.cls.veh;

//Complex Hierarchy Example
// Complex sealed hierarchy
public abstract sealed class Vehicle permits Car, Truck, Motorcycle {

    private final String manufacturer;

    public Vehicle(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public abstract void startEngine();
}
