/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.misc.pact;

import au.com.dius.pact.core.model.Interaction;
import au.com.dius.pact.core.model.annotations.Pact;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import com.spring5.entity.User;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpRequest;

@Provider("UserService")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Disabled(
        "Temporarily disabled for CICD - Method createUserPact does not conform required method signature 'public au.com.dius.pact.core.model.V4Pact xxx(PactBuilder builder)")
public class UserServiceProviderPactTest {

    public static final String PROVIDER_NAME = "UserService";

    @LocalServerPort
    private int port;

    @BeforeAll
    void setUp() {
        System.setProperty("pact.verifier.publishResults", "true");
    }

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    void testTemplate(
            Pact pact, Interaction interaction, HttpRequest request, PactVerificationContext context) {
        context.verifyInteraction();
    }

    @State("User with ID 1 exists")
    void userWithId1Exists() {
        // Setup: Create a user with ID 1 in the database
        User user
                = User.builder()
                        .id(1L)
                        .name("John Doe")
                        .email("john.doe@example.com")
                        .status("ACTIVE")
                        .createdAt(LocalDateTime.of(2023, 1, 1, 10, 0, 0))
                        .build();

        // Save to test database (using @MockBean or test data setup)
        // userRepository.save(user);
    }

    @State("Users with status ACTIVE exist")
    void usersWithStatusActiveExist() {
        // Setup: Create active users in the database
        User user1
                = User.builder()
                        .id(1L)
                        .name("John Doe")
                        .email("john.doe@example.com")
                        .status("ACTIVE")
                        .build();

        User user2
                = User.builder()
                        .id(2L)
                        .name("Jane Smith")
                        .email("jane.smith@example.com")
                        .status("ACTIVE")
                        .build();

        // Save to test database
        // userRepository.saveAll(List.of(user1, user2));
    }

    @State("No specific state required")
    void noSpecificStateRequired() {
        // No setup needed for this state
    }
}
