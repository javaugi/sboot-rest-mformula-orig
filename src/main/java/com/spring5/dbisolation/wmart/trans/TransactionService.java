/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.wmart.trans;

import com.spring5.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@Slf4j
public class TransactionService {

	private final TransactionRepository transactionRepository;

	private final TransactionMapper transactionMapper;

	private final UserService userService;

	public TransactionService(TransactionRepository transactionRepository, TransactionMapper transactionMapper,
			UserService userService) {
		this.transactionRepository = transactionRepository;
		this.transactionMapper = transactionMapper;
		this.userService = userService;
	}

	public Page<TransactionDto> getTransactions(TransactionQueryCriteria criteria, Pageable pageable) {
		log.info("Fetching transactions for user: {} with criteria: {}", criteria.getUserId(), criteria);

		// Validate user exists
		userService.validateUserExists(Long.valueOf(criteria.getUserId()));

		// Convert to domain criteria and fetch
		Page<Transaction> transactions = transactionRepository.findByCriteria(criteria, pageable);

		return transactions.map(transactionMapper::toDto);
	}

}
