/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.interview.hrank.veh.perfreview;

import com.spring5.entity.BonusMultiplier;
import com.spring5.repository.BonusRepository;
import com.spring5.service.PerformanceReviewImprovedService;
import com.spring5.validatorex.InvalidReviewScoreException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

// 1. Mark this class to use Testcontainers and Spring Boot
@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest // Loads the full application context
@Transactional // Rolls back transaction after each test, ensuring clean state
public class PerformanceReviewServiceSpringBootTestContainersIT {

	// 2. Define the container instance. This will start a PostgreSQL Docker container.
	@Container
	public static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
		.withDatabaseName("testdb")
		.withUsername("test")
		.withPassword("test");

	// 3. Dynamically override Spring DataSource properties to point to the Testcontainer
	@DynamicPropertySource
	static void configureProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", postgres::getJdbcUrl);
		registry.add("spring.datasource.username", postgres::getUsername);
		registry.add("spring.datasource.password", postgres::getPassword);
		// You can add other properties like Hibernate dialect if needed
		// registry.add("spring.jpa.properties.hibernate.dialect", () ->
		// "org.hibernate.dialect.PostgreSQLDialect");
	}

	// 4. Autowire the actual service from the Spring context
	// This will have the REAL DatabaseBonusConfig and BonusRepository injected
	@Autowired
	private PerformanceReviewImprovedService performanceReviewService;

	@Autowired
	private BonusRepository bonusRepository; // To set up test data

	// 5. Populate the test database before each test
	@BeforeEach
	void setUp() {
		// Clear and reset data for each test.
		// @Transactional ensures this is rolled back.
		bonusRepository.deleteAllInBatch();

		bonusRepository.save(new BonusMultiplier("Exceeds", 1.20));
		bonusRepository.save(new BonusMultiplier("Meets", 1.00));
		bonusRepository.save(new BonusMultiplier("Needs Improvement", 0.50));
		// Flushing ensures these are written to the DB before the tests run
		bonusRepository.flush();
	}

	// 6. Test the complete flow with a real database
	@Test
	void calculateBonus_WithExceedsScore_ReturnsCorrectMultiplierFromRealDb() throws InvalidReviewScoreException {
		// Act
		double result = performanceReviewService.calculateBonus("Exceeds");

		// Assert
		assertThat(result).isEqualTo(1.20);
	}

	@Test
	void calculateBonus_WithMeetsScore_ReturnsCorrectMultiplierFromRealDb() throws InvalidReviewScoreException {
		// Act
		double result = performanceReviewService.calculateBonus("Meets");

		// Assert
		assertThat(result).isEqualTo(1.00);
	}

	@Test
	void calculateBonus_WithInvalidScore_ThrowsException() {
		// Act & Assert
		assertThatThrownBy(() -> performanceReviewService.calculateBonus("Invalid Score"))
			.isInstanceOf(InvalidReviewScoreException.class)
			.hasMessageContaining("Invalid performance score: Invalid Score");
	}

	// 7. Test that the service can handle new data added dynamically to the DB
	@Test
	void calculateBonus_WithNewlyAddedScore_ReturnsNewMultiplier() throws InvalidReviewScoreException {
		// Arrange: Add a new bonus rule at runtime
		bonusRepository.save(new BonusMultiplier("Super Exceeds", 1.50));

		// Act & Assert: The service should be able to find it without any code changes
		double result = performanceReviewService.calculateBonus("Super Exceeds");
		assertThat(result).isEqualTo(1.50);
	}

}
