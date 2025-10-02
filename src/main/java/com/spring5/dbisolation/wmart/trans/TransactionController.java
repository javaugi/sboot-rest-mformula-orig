/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.wmart.trans;

import com.stripe.param.PaymentIntentConfirmParams.PaymentMethodOptions.AcssDebit.MandateOptions.TransactionType;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/*
Key Best Practices Implemented:
    Separation of Concerns: Clear separation between controller, service, and repository layers
    DTO Pattern: Separate API models from domain entities
    Validation: Comprehensive input validation at multiple levels
    Pagination: Proper Spring Data pagination with custom response wrapper
    Error Handling: Global exception handling with consistent error responses
    Performance: Dynamic query building with proper indexing considerations
    Logging: Strategic logging for debugging and monitoring
    Immutability: Use of records and final fields where appropriate
    Dependency Injection: Constructor injection for better testability
    API Versioning: URL versioning for future compatibility
    This implementation addresses all the discussion points while following Spring Boot and general software engineering best practices.
 */
@RestController
@RequestMapping("/api/v1/users/{userId}/transactions")
@Validated
public class TransactionController {

	private final TransactionService transactionService;

	private final TransactionValidator transactionValidator;

	public TransactionController(TransactionService transactionService, TransactionValidator transactionValidator) {
		this.transactionService = transactionService;
		this.transactionValidator = transactionValidator;
	}

	/*
	 * Type 1: API Implementation Example Question:
	 * "Design an endpoint that returns a user's recent transactions with filtering capabilities."
	 * 
	 * // Discussion points: // - Pagination implementation // - Filter validation // -
	 * Performance considerations (indexing) // - Error handling
	 */
	@GetMapping
	public ResponseEntity<PageResponse<TransactionDto>> getTransactions(@PathVariable @NotBlank String userId,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
			@RequestParam(required = false) TransactionType type,
			@PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

		// Validate input parameters
		transactionValidator.validateTransactionQuery(userId, startDate, endDate);

		// Build query criteria
		TransactionQueryCriteria criteria = TransactionQueryCriteria.builder()
			.userId(userId)
			.startDate(startDate)
			.endDate(endDate)
			.type(type)
			.build();

		// Fetch transactions
		Page<TransactionDto> transactions = transactionService.getTransactions(criteria, pageable);

		return ResponseEntity.ok(PageResponse.from(transactions));
	}

}
