/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.controller;

import com.spring5.dbisolation.wmart.Order;
import com.spring5.dto.CheckoutRequest;
import com.spring5.service.CheckoutService;
import com.spring5.utils.pact.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/checkout")
@CrossOrigin(origins = "*")
public class CheckoutController {

	@Autowired
	private CheckoutService checkoutService;

	@PostMapping
	public ApiResponse<Order> checkout(@RequestBody CheckoutRequest request) {
		try {
			Order order = checkoutService.processCheckout(request.getCustomerInfo(), request.getPaymentInfo());
			return ApiResponse.success("Order placed successfully!", order);
		}
		catch (Exception e) {
			return ApiResponse.error(e.getMessage());
		}
	}

}

/*
 * Features of this Spring Boot Backend: RESTful APIs for all operations Product
 * Management with variants Shopping Cart with add, update, remove operations Checkout
 * Process with order creation Error Handling with proper HTTP status codes CORS
 * Configuration for frontend integration In-Memory Storage (can be replaced with
 * database) Input Validation and error responses Clean Architecture with separation of
 * concerns
 * 
 * To run the application: Use Spring Boot Maven plugin: mvn spring-boot:run Or build and
 * run the JAR: mvn package && java -jar target/online-shopping-1.0.0.jar
 * 
 * The backend will be available at http://localhost:8080 and provides all the necessary
 * endpoints for the frontend to function properly.
 */
