/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.kafkamicroservice;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class PaymentRequest {

	private String id;

	private BigDecimal amount; // Amount in major currency units (e.g., USD)

}
