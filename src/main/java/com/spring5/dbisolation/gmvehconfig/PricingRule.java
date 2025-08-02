/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.gmvehconfig;

import java.math.BigDecimal;

/**
 *
 * @author javau
 */
public interface PricingRule {
    BigDecimal apply(VehicleConfig config, BigDecimal basePrice);
}
