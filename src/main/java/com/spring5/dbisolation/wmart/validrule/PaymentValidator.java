/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.spring5.dbisolation.wmart.validrule;

import com.spring5.kafkamicroservice.Payment;
import java.util.List;

/**
 * @author javau
 */
public interface PaymentValidator {

	boolean validatePayment(Payment payment);

	List<ValidationRule> getFailedRules(Payment payment);

}
