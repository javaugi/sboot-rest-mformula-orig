/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.wmart.trans;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class WmGlobalExceptionHandler {

	@ExceptionHandler(InvalidDateRangeException.class)
	public ResponseEntity<ErrorResponse> handleInvalidDateRange(InvalidDateRangeException ex) {
		log.warn("Invalid date range requested: {}", ex.getMessage());
		return ResponseEntity.badRequest().body(new ErrorResponse("INVALID_DATE_RANGE", ex.getMessage()));
	}

	@ExceptionHandler(QueryRangeTooLargeException.class)
	public ResponseEntity<ErrorResponse> handleQueryRangeTooLarge(QueryRangeTooLargeException ex) {
		log.warn("Query range too large: {}", ex.getMessage());
		return ResponseEntity.badRequest().body(new ErrorResponse("QUERY_RANGE_TOO_LARGE", ex.getMessage()));
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
		log.warn("Constraint violation: {}", ex.getMessage());
		return ResponseEntity.badRequest().body(new ErrorResponse("VALIDATION_ERROR", "Invalid request parameters"));
	}

}
