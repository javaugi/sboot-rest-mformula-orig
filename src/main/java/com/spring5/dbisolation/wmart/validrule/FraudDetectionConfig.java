/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.wmart.validrule;

/**
 *
 * @author javau
 */
public class FraudDetectionConfig {
    public Double getThreshold(String largeTransaction) {
        switch (largeTransaction) {
            case "largeTrabsaction":
                return 10000d;
            case "mediumTrabsaction":
                return 5000d;
            default:
                return 100d;
        }
    }
}
