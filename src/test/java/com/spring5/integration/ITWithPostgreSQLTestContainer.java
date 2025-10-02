/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.integration;

import com.spring5.entity.Customer;
import com.spring5.repository.CustomerRepository;
import java.util.Optional;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
public class ITWithPostgreSQLTestContainer {

	@Container
	public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:17")
		.withDatabaseName("testdb")
		.withUsername("postgres")
		.withPassword("admin");

	@DynamicPropertySource
	public static void postgresqlProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
		registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
		registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
	}

	@Autowired
	private CustomerRepository customerRepository;

	@Test
	public void shouldSaveAndRetrieveCustomer() {
		// Given
		Customer customer = new Customer(2L, "Jane Smith", "jane@example.com");

		// When
		Customer saved = customerRepository.save(customer);
		Optional<Customer> found = customerRepository.findByEmail("jane@example.com");

		// Then
		assertTrue(saved != null && saved.getId() != null);
		assertThat(found).isPresent();
		assertThat(found.get().getName()).isEqualTo("Jane Smith");
	}

}
