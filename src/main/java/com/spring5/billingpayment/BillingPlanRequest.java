/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.billingpayment;

// Request DTO
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BillingPlanRequest {

    private String planName;
    private String description;
    private String amount;
    private String agreementName;
    private String startDate; // ISO-8601 format
}
