/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation;

import com.spring5.entity.Product;
import com.spring5.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/*
Global Configuration - You can also set the default isolation level in your application.properties:
        properties
        # Default isolation level for all transactions
        spring.jpa.properties.hibernate.connection.isolation=2
    Where the values correspond to:
        1: READ_UNCOMMITTED
        2: READ_COMMITTED
        4: REPEATABLE_READ
        8: SERIALIZABLE
 */
@Service
@RequiredArgsConstructor
public class ProgrammaticTransMan {

	private final PlatformTransactionManager transactionManager;

	private final ProductRepository productRepository;

	// private final JpaTransactionManager transactionManager;
	public void updateProductWithCustomIsolation(Product product) {
		TransactionDefinition definition = new DefaultTransactionDefinition();
		((DefaultTransactionDefinition) definition).setIsolationLevel(Isolation.SERIALIZABLE.value());

		TransactionStatus status = transactionManager.getTransaction(definition);

		try {
			productRepository.save(product);
			transactionManager.commit(status);
		}
		catch (Exception e) {
			transactionManager.rollback(status);
			throw e;
		}
	}

}
