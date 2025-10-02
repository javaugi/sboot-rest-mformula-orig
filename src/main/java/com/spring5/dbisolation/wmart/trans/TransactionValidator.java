/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.wmart.trans;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TransactionValidator {

	private static final int MAX_QUERY_RANGE_DAYS = 365;

	public void validateTransactionQuery(String userId, LocalDate startDate, LocalDate endDate) {
		validateDateRange(startDate, endDate);
		validateQueryRange(startDate, endDate);
	}

	private void validateDateRange(LocalDate startDate, LocalDate endDate) {
		if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
			throw new InvalidDateRangeException("Start date cannot be after end date");
		}
	}

	private void validateQueryRange(LocalDate startDate, LocalDate endDate) {
		if (startDate != null && endDate != null) {
			long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
			if (daysBetween > MAX_QUERY_RANGE_DAYS) {
				throw new QueryRangeTooLargeException("Query range cannot exceed " + MAX_QUERY_RANGE_DAYS + " days");
			}
		}
	}

}
