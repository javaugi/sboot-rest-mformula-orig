/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.wmart.validrule;

import com.spring5.kafkamicroservice.Payment;

/**
 * @author javau
 */
public interface ValidationRule {
    // Returns true if the payment passes the rule, false otherwise

    boolean validate(Payment payment);

    // Returns the name or a unique ID of the rule
    String getRuleName();
}
