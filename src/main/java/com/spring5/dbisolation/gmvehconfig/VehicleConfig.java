/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.gmvehconfig;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;

@Builder(toBuilder = true)
@Data
public class VehicleConfig {

    Long id;
    BigDecimal basePrice;
}
