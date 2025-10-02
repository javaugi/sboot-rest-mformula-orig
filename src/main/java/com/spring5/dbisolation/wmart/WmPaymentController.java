/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.wmart;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Mono;

// @RestController
public class WmPaymentController {

	private final WmPaymentService paymentService;

	public WmPaymentController(WmPaymentService paymentService) {
		this.paymentService = paymentService;
	}

	// A non-blocking, reactive endpoint to handle payment requests.
	@PostMapping("/payments")
	public Mono<ResponseEntity<PaymentResponse>> processPayment(@RequestBody Mono<PaymentRequest> request) {
		// The service layer handles the entire async, non-blocking workflow.
		// It returns a Mono, which is a stream of 0 or 1 item.
		return request.flatMap(paymentService::processPayment)
			.map(result -> ResponseEntity.status(HttpStatus.CREATED).body(result))
			.onErrorResume(e -> {
				// Handle errors gracefully and return an appropriate status code.
				return Mono.just(ResponseEntity.badRequest().body(new PaymentResponse("failed", e.getMessage())));
			});
	}

}
