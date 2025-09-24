/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.wmart.validrule;

public class LargeTransactionRule implements FraudRule {

    @Override
    public boolean isSuspicious(Transaction transaction, FraudDetectionConfig config) {
        return transaction.getAmount() > config.getThreshold("largeTransaction");
    }
}
