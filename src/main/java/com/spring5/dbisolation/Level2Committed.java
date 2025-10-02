/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation;

import com.spring5.entity.Account;
import com.spring5.repository.AccountRepository;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class Level2Committed {

	private final AccountRepository accountRepository;

	@Transactional(isolation = Isolation.READ_COMMITTED)
	public void transferMoney(Long fromId, Long toId, BigDecimal amount) {
		// These reads will only see committed data
		Account fromAccount = accountRepository.findById(fromId).orElseThrow();
		Account toAccount = accountRepository.findById(toId).orElseThrow();

		fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
		toAccount.setBalance(toAccount.getBalance().add(amount));

		accountRepository.save(fromAccount);
		accountRepository.save(toAccount);

		// Between these operations, other transactions may see the committed changes
	}

}
