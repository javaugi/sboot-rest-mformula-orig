/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.billingpayment;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentError {

	private String code; // Stripe error code (e.g., "card_declined")

	private String message; // More detailed error message

	private String declineCode; // Card-specific decline code (e.g., "do_not_honor")

	private String param; // Parameter that caused the error

}
