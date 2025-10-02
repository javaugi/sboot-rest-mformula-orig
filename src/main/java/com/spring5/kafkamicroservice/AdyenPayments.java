/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.kafkamicroservice;

import lombok.Data;

@Data
public class AdyenPayments {

	private AdyenPaymentRequest request;

	private AdyenClient adyenClient;

	private String apiKey;

	private String merchantAccount;

	private String environment;

	private String endpoint;

	public AdyenPayments(AdyenPaymentRequest request) {
		this.request = request;
	}

	public AdyenPaymentResponse payments(AdyenPaymentRequests requests) {
		return null;
	}

	public AdyenPayments(AdyenClient adyenClient) {
		this.adyenClient = adyenClient;
	}

}
