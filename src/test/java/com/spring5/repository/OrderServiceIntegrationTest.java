/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.repository;

import com.spring5.audit.AuditOrder;
import com.spring5.audit.OrderRequest;
import com.spring5.audit.OrderService;
import com.spring5.validatorex.PaymentException;
import javax.transaction.Transactional;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Transactional
public class OrderServiceIntegrationTest {

	@Autowired
	private OrderService orderService;

	@Autowired
	private OrderRepository orderRepository;

	// @Test
	public void shouldProcessOrderSuccessfully() {
		OrderRequest request = new OrderRequest(/* test data */);
		AuditOrder order = orderService.processOrder(request);

		assertThat(order).isNotNull();
		assertThat(order.getStatus()).isEqualTo("PROCESSED");
		assertThat(orderRepository.count()).isEqualTo(1);
	}

	// @Test
	public void shouldRollbackWhenPaymentFails() {
		OrderRequest request = new OrderRequest(/*
												 * test data that will cause payment to
												 * fail
												 */);

		assertThatThrownBy(() -> orderService.processOrder(request)).isInstanceOf(PaymentException.class);

		assertThat(orderRepository.count()).isEqualTo(0);
	}

}
