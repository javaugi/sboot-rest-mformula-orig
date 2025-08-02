/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.integration;

import com.spring5.entity.Customer;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/*
Best Practices
    Use @DataJpaTest for repository layer tests with H2
    Use Testcontainers for integration tests that need real database behavior
    Reuse containers between tests when possible (declare containers as static)
    Clean up data after each test with @Transactional or manual cleanup
    Consider test profiles to separate different test configurations
*/

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class ITRestControllerTestWithTestcontainers {
 
    @Container
    public static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17")
        .withDatabaseName("testdb")
        .withUsername("postgres")
        .withPassword("admin");
    
    @DynamicPropertySource
    public static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    public void shouldCreateAndGetCustomer() {
        // Create customer
        Customer customer = new Customer(5L, "API Test", "api@test.com");
        ResponseEntity<Customer> createResponse = restTemplate.postForEntity(
            "/api/customers", 
            customer, 
            Customer.class);
        
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        
        // Get customer
        Long customerId = createResponse.getBody().getId();
        ResponseEntity<Customer> getResponse = restTemplate.getForEntity(
            "/api/customers/" + customerId, 
            Customer.class);
        
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody().getEmail()).isEqualTo("api@test.com");
    }    
}
