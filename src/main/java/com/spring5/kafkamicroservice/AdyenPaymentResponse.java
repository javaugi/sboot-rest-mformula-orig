/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.kafkamicroservice;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdyenPaymentResponse {

	private String pspReference;

	private BigDecimal amount;

	private boolean authorised;

	private String refusalReason;

	private String paymentId;

	public AdyenPaymentResponse(String pspReference) {
		this.pspReference = pspReference;
	}

	public static AdyenPaymentResponse fromAdyenResponse(AdyenPaymentResponse response) {
		return response;
	}

	public static AdyenPaymentResponse pending(String paymentId) {
		return AdyenPaymentResponse.builder().paymentId(paymentId).build();
	}

}
