/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.misc.hackerrank;

import com.spring5.entity.BonusMultiplier;
import com.spring5.service.DatabaseBonusConfig;
import com.spring5.repository.BonusRepository;
import com.spring5.validatorex.InvalidReviewScoreException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

// 1. Annotation to setup a JPA test context with an embedded database (H2)
@DataJpaTest
// 2. Load specific test properties, especially if you want to see generated SQL
@TestPropertySource(properties = {
    "spring.jpa.show-sql=true", // Optional: great for debugging
    "spring.jpa.properties.hibernate.format_sql=true" // Optional: prettifies SQL logs
})
// 3. This is CRUCIAL: It imports the component under test (DatabaseBonusConfig) into the JPA test context.
// It will automatically get the BonusRepository bean injected.
@Import(DatabaseBonusConfig.class)
@Disabled("Temporarily disabled for CICD")
class DatabaseBonusConfigDataJpaITests {

    // 4. Autowire the component we are testing
    @Autowired
    private DatabaseBonusConfig databaseBonusConfig;

    // 5. Autowire the repository to set up test data
    @Autowired
    private BonusRepository bonusRepository;

    // 6. Method to run BEFORE each test to populate the database
    @BeforeEach
    void setUp() {
        // Clear any existing data and insert our test fixtures
        bonusRepository.deleteAllInBatch(); // Faster than deleteAll()

        bonusRepository.save(new BonusMultiplier("Exceeds", 1.20));
        bonusRepository.save(new BonusMultiplier("Meets", 1.00));
        bonusRepository.save(new BonusMultiplier("Needs Improvement", 0.50));
        // Flushing ensures these are written to the DB before the tests run
        bonusRepository.flush();
    }

    // 7. Test the successful "happy path" scenarios
    @Test
    void getMultiplierForScore_ValidScore_ReturnsCorrectMultiplier() throws InvalidReviewScoreException {
        // Act & Assert
        assertThat(databaseBonusConfig.getMultiplierForScore("Exceeds")).isEqualTo(1.20);
        assertThat(databaseBonusConfig.getMultiplierForScore("Meets")).isEqualTo(1.00);
        assertThat(databaseBonusConfig.getMultiplierForScore("Needs Improvement")).isEqualTo(0.50);
    }

    // 8. Test the edge case: score not found in the database
    @Test
    void getMultiplierForScore_InvalidScore_ThrowsInvalidReviewScoreException() {
        // Act & Assert: Use AssertJ for fluent exception assertions
        assertThatThrownBy(() -> databaseBonusConfig.getMultiplierForScore("Does Not Exist"))
            .isInstanceOf(InvalidReviewScoreException.class)
            .hasMessageContaining("Invalid performance score: Does Not Exist");
    }

    // 9. Test the edge case: what if the database is empty? (Optional is empty)
    @Test
    void getMultiplierForScore_ScoreNotFoundInEmptyTable_ThrowsException() {
        // Arrange: Clear the specific data for this test
        bonusRepository.deleteAllInBatch();

        // Act & Assert
        assertThatThrownBy(() -> databaseBonusConfig.getMultiplierForScore("Exceeds"))
            .isInstanceOf(InvalidReviewScoreException.class);
    }

    // 10. (BONUS) Test case sensitivity if it's a business requirement
    @Test
    void getMultiplierForScore_ScoreIsCaseSensitive_ThrowsExceptionForWrongCase() {
        // Our database has 'Exceeds', not 'exceeds'
        assertThatThrownBy(() -> databaseBonusConfig.getMultiplierForScore("exceeds"))
            .isInstanceOf(InvalidReviewScoreException.class);
    }
}
