/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.validatorex;

import com.spring5.billingpayment.BillingErrorResponse;
import com.spring5.utils.pact.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(IllegalArgumentException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiResponse<String> handleIllegalArgumentException(IllegalArgumentException e) {
		return ApiResponse.error(e.getMessage());
	}

	@ExceptionHandler(IllegalStateException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiResponse<String> handleIllegalStateException(IllegalStateException e) {
		return ApiResponse.error(e.getMessage());
	}

	/*
	 * @ExceptionHandler(Exception.class)
	 * 
	 * @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) public ApiResponse<String>
	 * handleGenericException(Exception e) { return
	 * ApiResponse.error("An unexpected error occurred: " + e.getMessage()); } //
	 */
	@ExceptionHandler(BillingException.class)
	public ResponseEntity<?> handleBillingException(BillingException ex) {
		return ResponseEntity.status(ex.getStatus())
			.body(new BillingErrorResponse("Billing processing error", ex.getMessage()));
	}

	/*
	 * @ExceptionHandler(Exception.class) public ResponseEntity<?>
	 * handleGenericException(Exception ex) { return
	 * ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR) .body(new
	 * BillingErrorResponse( "Internal server error", ex.getMessage()) ); } //
	 */
	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ProblemDetail handleGenericException(Exception ex) {
		ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
				"An unexpected error occurred.");
		problemDetail.setTitle("Internal Server Error");
		// Log the exception details
		return problemDetail;
	}

	/*
	 * @ExceptionHandler(MethodArgumentNotValidException.class) public
	 * ResponseEntity<Map<String, String>> handleValidationExceptions(
	 * MethodArgumentNotValidException ex) { Map<String, String> errors = new HashMap<>();
	 * ex.getBindingResult().getAllErrors().forEach((error) -> { String fieldName =
	 * ((FieldError) error).getField(); String errorMessage = error.getDefaultMessage();
	 * errors.put(fieldName, errorMessage); }); return
	 * ResponseEntity.badRequest().body(errors); } //
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ProblemDetail handleMethodValidationExceptions(MethodArgumentNotValidException ex) {
		ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Validation failed");
		problemDetail.setTitle("Invalid Input");
		// Optionally add field errors
		// problemDetail.setProperty("errors",
		// ex.getBindingResult().getFieldErrors().stream()
		// .map(fieldError -> fieldError.getField() + ": " +
		// fieldError.getDefaultMessage())
		// .collect(Collectors.toList()));
		return problemDetail;
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<Map<String, String>> handleConstraintViolationException(ConstraintViolationException ex) {
		Map<String, String> errors = new HashMap<>();
		ex.getConstraintViolations().forEach(violation -> {
			String fieldName = violation.getPropertyPath().toString();
			String errorMessage = violation.getMessage();
			errors.put(fieldName, errorMessage);
		});
		return ResponseEntity.badRequest().body(errors);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<String> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
		return ResponseEntity.badRequest().body("Invalid JSON format: " + ex.getMostSpecificCause().getMessage());
	}

	@ExceptionHandler(HttpClientErrorException.class)
	public ResponseEntity<Map<String, String>> handlehttpClientExceptions(MethodArgumentNotValidException ex) {
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getAllErrors().forEach((error) -> {
			String fieldName = ((FieldError) error).getField();
			String errorMessage = error.getDefaultMessage();
			errors.put(fieldName, errorMessage);
		});
		return ResponseEntity.badRequest().body(errors);
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public ProblemDetail handleResourceNotFoundException(ResourceNotFoundException ex) {
		ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
		problemDetail.setTitle("Resource Not Found");
		// Add more details if needed
		return problemDetail;
	}

}
