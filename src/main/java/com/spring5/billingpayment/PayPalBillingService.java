/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.billingpayment;

import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import com.spring5.validatorex.BillingException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class PayPalBillingService {

	private final APIContext apiContext;

	@Autowired
	public PayPalBillingService(@Value("${paypal.client-id}") String clientId,
			@Value("${paypal.client-secret}") String clientSecret, @Value("${paypal.mode}") String mode) {
		this.apiContext = new APIContext(clientId, clientSecret, mode);
	}

	// Fallback method
	public BillingAgreementResponse createBillingAgreementFallback(BillingPlanRequest request, Exception ex) {
		return new BillingAgreementResponse(null, "FAILED", null,
				"Service unavailable. Please try again later. Error: " + ex.getMessage());
	}

	// paypalService is the instance name defined in the application.yml
	// Reuse Across Methods: Multiple methods in different classes can share the same
	// circuit breaker
	// instance if they use the same name.
	@Retry(name = "paypalService")
	@CircuitBreaker(name = "paypalService", fallbackMethod = "createBillingAgreementFallback")
	public BillingAgreementResponse createBillingAgreement(BillingPlanRequest request) {
		try {
			// Create Plan
			Plan plan = new Plan();
			plan.setName(request.getPlanName());
			plan.setDescription(request.getDescription());
			plan.setType("INFINITE"); // For subscription model

			List<PaymentDefinition> paymentDefinitions = createPaymentDefinitions(request);
			plan.setPaymentDefinitions(paymentDefinitions);
			// Create plan
			plan = plan.create(apiContext);

			List<Patch> patchRequest = createPatchRequest();
			plan.update(apiContext, patchRequest);

			Agreement agreement = createAgreement(request);

			Plan planRef = new Plan();
			planRef.setId(plan.getId());
			agreement.setPlan(planRef);

			Payer payer = new Payer();
			payer.setPaymentMethod("paypal");
			agreement.setPayer(payer);

			// Execute agreement
			agreement = agreement.create(apiContext);

			// Return standardized response
			return new BillingAgreementResponse(agreement.getId(), agreement.getState(),
					agreement.getLinks()
						.stream()
						.filter(link -> "approval_url".equals(link.getRel()))
						.findFirst()
						.map(Links::getHref)
						.orElse(null),
					null);
		}
		catch (PayPalRESTException e) {
			throw new BillingException("PayPal API error: " + e.getMessage(), e,
					HttpStatus.valueOf(e.getResponsecode()));
		}
		catch (MalformedURLException | UnsupportedEncodingException e) {
			throw new BillingException("PayPal API error: " + e.getMessage(), e, HttpStatus.BAD_GATEWAY);
		}
	}

	private Agreement createAgreement(BillingPlanRequest request) {
		// Create agreement
		Agreement agreement = new Agreement();
		agreement.setName(request.getAgreementName());
		agreement.setDescription(request.getDescription());
		agreement.setStartDate(request.getStartDate());
		return agreement;
	}

	private List<Patch> createPatchRequest() {
		// Activate plan
		List<Patch> patchRequest = new ArrayList<>();
		Map<String, String> value = new HashMap<>();
		value.put("state", "ACTIVE");
		Patch patch = new Patch();
		patch.setOp("replace");
		patch.setPath("/");
		patch.setValue(value);
		patchRequest.add(patch);
		return patchRequest;
	}

	private List<PaymentDefinition> createPaymentDefinitions(BillingPlanRequest request) {
		// Set payment definitions
		PaymentDefinition paymentDefinition = new PaymentDefinition();
		paymentDefinition.setName("Regular Payments");
		paymentDefinition.setType("REGULAR");
		paymentDefinition.setFrequency("MONTH");
		paymentDefinition.setFrequencyInterval("1");
		paymentDefinition.setCycles("0"); // Infinite
		paymentDefinition.setAmount(new Currency().setValue(request.getAmount()).setCurrency("USD"));
		List<PaymentDefinition> paymentDefinitions = new ArrayList<>();
		paymentDefinitions.add(paymentDefinition);
		return paymentDefinitions;
	}

}

/*
 * In the @CircuitBreaker(name = "paypalService", ...) annotation, the name refers to a
 * configured circuit breaker instance in your Resilience4j configuration, not directly to
 * a class name. Here's the detailed explanation:
 * 
 * What paypalService Refers To: Configuration Identifier:
 * 
 * The name "paypalService" corresponds to a circuit breaker instance defined in your
 * configuration (e.g., application.yml or application.properties).
 * 
 * Example YAML configuration:
 * 
 * yaml resilience4j: circuitbreaker: instances: paypalService: # <-- This is what the
 * name refers to failureRateThreshold: 50 minimumNumberOfCalls: 5
 * waitDurationInOpenState: 5s Not the Class Name:
 * 
 * While you might name it after the service class (e.g., PayPalBillingService) for
 * clarity, it’s purely logical grouping.
 * 
 * You could name it "billingService" or "externalPaymentCB"—it just needs to match your
 * configuration.
 * 
 * Reuse Across Methods:
 * 
 * Multiple methods in different classes can share the same circuit breaker instance if
 * they use the same name.
 * 
 * Key Points: Purpose: The name links the annotation to a specific circuit breaker
 * configuration.
 * 
 * Scope: It’s application-wide, not class-specific.
 * 
 * Best Practice: Use descriptive names matching the external service or risk profile
 * (e.g., paypalService, databaseCB).
 * 
 * Example: How It Connects Configuration (application.yml):
 * 
 * yaml resilience4j: circuitbreaker: instances: paypalService: # <-- name referenced in
 * annotation failureRateThreshold: 50 waitDurationInOpenState: 10s patientDataService: #
 * Another instance failureRateThreshold: 30 Class Usage:
 * 
 * java
 * 
 * @Service public class PayPalBillingService {
 * 
 * @CircuitBreaker(name = "paypalService", fallbackMethod = "fallback") // Uses the
 * "paypalService" config public String createAgreement() { // Call PayPal API }
 * 
 * public String fallback(Exception ex) { return "Fallback response"; } } If You Prefer
 * Class-Name Alignment: You could name the circuit breaker instance after the class, but
 * this is optional:
 * 
 * yaml resilience4j: circuitbreaker: instances: PayPalBillingService: # Matches class
 * name failureRateThreshold: 50 Then use it as:
 * 
 * java
 * 
 * @CircuitBreaker(name = "PayPalBillingService", ...) Summary: The name is a logical
 * identifier for the circuit breaker configuration.
 * 
 * It’s defined in your Resilience4j config, not tied to class names (though you can align
 * them for clarity).
 * 
 * Multiple methods/classes can share the same circuit breaker instance by using the same
 * name.
 */
