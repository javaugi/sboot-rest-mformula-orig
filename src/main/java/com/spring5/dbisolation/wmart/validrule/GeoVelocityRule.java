/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.wmart.validrule;

public class GeoVelocityRule implements FraudRule {

    @Override
    public boolean isSuspicious(Transaction transaction, FraudDetectionConfig config) {
        // Placeholder logic: In a real-world scenario, this would check
        // the time between transactions and the distance between their locations.
        // Example:
        // return transaction.getTimeDelta(previousTransaction) < 1 minute &&
        //        transaction.getDistance(previousTransaction) > 1000 miles;
        return false;
    }
}
