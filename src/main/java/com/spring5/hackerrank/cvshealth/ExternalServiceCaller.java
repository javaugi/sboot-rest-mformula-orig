/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.hackerrank.cvshealth;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.vavr.CheckedFunction0;
import io.vavr.Function0;
import io.vavr.control.Try;
import java.time.Duration;

// import io.vavr.CheckedFunction0; // Assuming a Vavr CheckedFunction1
/**
 * @author javau
 */
public class ExternalServiceCaller {

	private final CircuitBreaker circuitBreaker;

	private final Retry retry;

	public ExternalServiceCaller() {
		// Configure Circuit Breaker
		CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
			.failureRateThreshold(50) // Percentage of failures above which the circuit
										// will open
			.waitDurationInOpenState(Duration.ofSeconds(5)) // Time the circuit will stay
															// open
			.permittedNumberOfCallsInHalfOpenState(3) // Number of calls allowed in
														// HALF_OPEN state
			.slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
			.slidingWindowSize(10) // Size of the sliding window
			.build();

		circuitBreaker = CircuitBreaker.of("externalService", circuitBreakerConfig);

		// Configure Retry
		RetryConfig retryConfig = RetryConfig.custom()
			.maxAttempts(3) // Maximum number of attempts
			.waitDuration(Duration.ofMillis(500)) // Time to wait between retries
			.retryExceptions(java.io.IOException.class, java.util.concurrent.TimeoutException.class)
			.build();

		retry = Retry.of("externalServiceRetry", retryConfig);

		// Optional: Add event listeners for logging
		circuitBreaker.getEventPublisher()
			.onStateTransition(event -> System.out
				.println("Circuit Breaker State Transition: " + event.getStateTransition().getFromState().name()
						+ " -> " + event.getStateTransition().getToState().name()));
		retry.getEventPublisher()
			.onRetry(event -> System.out.println("Retrying call: " + event.getNumberOfRetryAttempts() + " time"));
	}

	/*
	 * public String callExternalService() { CheckedFunction0<String> call =
	 * (CheckedFunction0<String>) CircuitBreaker.decorateCheckedSupplier(circuitBreaker,
	 * () -> { System.out.println("Attempting to call external service..."); // Simulate
	 * external service call that might fail if (Math.random() < 0.7) { // 70% chance of
	 * failure throw new java.io.IOException("Simulated external service error!"); }
	 * return "Data from external service"; });
	 * 
	 * // Decorate with Retry call = Retry.decorateCheckedFunction(retry, call);
	 * 
	 * return Try.of(call) .recover(throwable -> { System.err.
	 * println("External service call failed after retries and circuit breaker intervention: "
	 * + throwable.getMessage()); return "Fallback data"; // Provide a fallback }).get();
	 * } //
	 */

	/*
	 * public String callExternalService2() { CheckedFunction0<String> call =
	 * (CheckedFunction0<String>) CircuitBreaker.decorateCheckedFunction(circuitBreaker,
	 * (String) -> { System.out.println("Attempting to call external service..."); //
	 * Simulate external service call that might fail if (Math.random() < 0.7) { // 70%
	 * chance of failure throw new
	 * java.io.IOException("Simulated external service error!"); } return
	 * "Data from external service"; });
	 * 
	 * // Decorate with Retry call = retry.executeSupplier(call);
	 * 
	 * return Try.of(call) .recover(throwable -> { System.err.
	 * println("External service call failed after retries and circuit breaker intervention: "
	 * + throwable.getMessage()); return "Fallback data"; // Provide a fallback }).get();
	 * } //
	 */
	// *
	public String callExternalService() {
		/*
		 * CheckedSupplier<String> call = (CheckedSupplier<String>)
		 * CircuitBreaker.decorateCheckedSupplier(circuitBreaker, () -> {
		 * System.out.println("Attempting to call external service..."); // Simulate
		 * external service call that might fail if (Math.random() < 0.7) { // 70% chance
		 * of failure throw new java.io.IOException("Simulated external service error!");
		 * } return "Data from external service"; }); //
		 */

		Function0<String> call = (Function0<String>) CircuitBreaker.decorateCheckedFunction(circuitBreaker,
				(String) -> {
					System.out.println("Attempting to call external service...");
					// Simulate external service call that might fail
					if (Math.random() < 0.7) { // 70% chance of failure
						throw new java.io.IOException("Simulated external service error!");
					}
					return "Data from external service";
				});
		// Decorate with Retry
		CheckedFunction0<String> call0 = null; // Retry.decorateFunction(retry, call);

		return Try.of(call0).recover(throwable -> {
			System.err.println("External service call failed after retries and circuit breaker intervention: "
					+ throwable.getMessage());
			return "Fallback data"; // Provide a fallback
		}).get();
	}

	// */
	public static void main(String[] args) throws InterruptedException {
		ExternalServiceCaller caller = new ExternalServiceCaller();
		for (int i = 0; i < 20; i++) {
			// System.out.println("Result " + i + ": " + caller.callExternalService());
			System.out.println("Result " + i + ": ");
			Thread.sleep(500); // Simulate some delay between calls
		}
	}

}
