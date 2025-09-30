/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.gmvehconfig;

import java.math.BigDecimal;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class RegionalAdjustmentRule implements PricingRule {

    @Override
    public BigDecimal apply(VehicleConfig config, BigDecimal basePrice) {
        // Apply region-based adjustments
        return BigDecimal.TEN;
    }
}
