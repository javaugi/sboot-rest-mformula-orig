/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.kafkamicroservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author javaugi
 */
public class PaymentSecPciDssSecretMan {

	// See PaymentService and PaymentStripeConfig

	private static final Logger log = LoggerFactory.getLogger(PaymentSecPciDssSecretMan.class);

	// 4. Security - PCI DSS Compliance: See PaymentService
	class PaymentService {

		// Never log full payment details

		public void process(PaymentRequest request) {
			log.info("Processing payment {} for amount {}", maskPaymentId(request.getId()), // Shows
																							// only
																							// last
																							// 4
																							// digits
					request.getAmount());
		}

		private String maskPaymentId(String id) {
			return "****" + id.substring(id.length() - 4);
		}

	}

	// Secret Management: -- see PaymentStripeConfig
	@Configuration
	@ConfigurationProperties(prefix = "payment.stripe")
	class StripeConfig {

		@Value("${vault.stripe.api-key}")
		private String apiKey;

		// ...

	}

}
