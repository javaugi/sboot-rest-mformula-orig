/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.repository;

import com.spring5.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@DataJpaTest
@Testcontainers
public class UserRepositoryPostgresTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15.3")
            .withDatabaseName("RPG_MOCK")
            .withUsername("postgres")
            .withPassword("admin");

    @DynamicPropertySource
    public static void configure(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private UserRepository userRepository;

    /*
    What This Does:
       Spins up a real PostgreSQL container for the test.
       Injects the container’s JDBC URL, user, and password into the Spring context.
       Runs full-blown JPA queries against that live DB.
       No need for H2 or mocking — this mirrors production-like behavior.

   💡 Tips:
       Use @Testcontainers and @Container only once per test class.
       Testcontainers automatically shuts down the container after the test.
       You can also use MySQL, MongoDB, Kafka, Redis, etc. with similar patterns.

    Would you like an example using Testcontainers + Spring Boot @SpringBootTest instead of @DataJpaTest, or for MongoDB/CosmosDB/Kafka?
     */
    //@Test
    public void testCreateAndFindUser() {
        User user = new User("Alice", "email@email.com");
        userRepository.save(user);

        var found = userRepository.findByName("Alice");
        //assertThat(found).isPresent();
        //assertThat(found.get().getName()).isEqualTo("Alice");
    }
}
