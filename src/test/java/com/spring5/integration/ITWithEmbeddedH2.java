/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.integration;

import com.spring5.entity.Customer;
import com.spring5.repository.CustomerRepository;
import java.util.Optional;
import javax.sql.DataSource;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ITWithEmbeddedH2 {

	@Autowired
	private CustomerRepository customerRepository;

	@Test
	public void shouldSaveAndRetrieveCustomer() {
		// Given
		Customer customer = new Customer(1L, "John Doe", "john@example.com");

		// When
		Customer saved = customerRepository.save(customer);
		Optional<Customer> found = customerRepository.findByEmail("john@example.com");

		// Then
		assertThat(found).isPresent();
		assertThat(found.get().getName()).isEqualTo("John Doe");
	}

	@TestConfiguration
	public static class TestConfig {

		@Bean
		public DataSource dataSource() {
			return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2)
				.addScript("classpath:schema.sql")
				.build();
		}

	}

}
