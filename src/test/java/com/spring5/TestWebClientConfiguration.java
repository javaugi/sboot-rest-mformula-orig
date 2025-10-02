/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5;

import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@TestConfiguration
public class TestWebClientConfiguration {

	@Test
	public void contextLoads() {
	}

	@Bean
	public WebTestClient webTestClient(WebApplicationContext context) {
		return WebTestClient.bindToApplicationContext(context).build();
	}

	public void dataJpaTestVsSpringBootTest() {
		/*
		 * The difference between @DataJpaTest and @SpringBootTest in Spring Boot testing
		 * boils down to scope, startup time, and test intent.
		 * 
		 * ✅ @DataJpaTest: Focused JPA Repository Testing Use this when testing only the
		 * JPA layer (repositories/entities).
		 * 
		 * 🔍 Features: Loads only JPA components (@Entity, @Repository, DataSource,
		 * Hibernate, etc.). Uses in-memory DB by default (like H2), unless overridden
		 * (e.g., with Testcontainers). Faster to run. Rolls back transactions by default
		 * after each test.
		 * 
		 * ✅ Example Use:
		 * 
		 * @DataJpaTest public class UserRepositoryTest {
		 * 
		 * @Autowired private UserRepository userRepository;
		 * 
		 * @Test void testSave() { userRepository.save(new User("Alice")); } }
		 * ✅ @SpringBootTest: Full Application Context Use this when testing the whole
		 * Spring Boot app, such as services, controllers, and repositories together.
		 * 
		 * 🔍 Features: Loads the entire Spring context, including: Controllers Services
		 * Repositories Custom beans Suitable for integration tests, end-to-end tests, and
		 * multi-layer tests. Slower to start, but more realistic.
		 * 
		 * ✅ Example Use:
		 * 
		 * @SpringBootTest public class UserServiceIntegrationTest {
		 * 
		 * @Autowired private UserService userService;
		 * 
		 * @Test void testUserCreationFlow() { userService.createUser("Bob"); } } ⚖️
		 * Comparison Table Feature @DataJpaTest @SpringBootTest Scope JPA Layer Only Full
		 * Application Context Beans Loaded @Entity, @Repository, DataSource Everything in
		 * the app Startup Time Fast Slower Default DB In-memory (H2) Configurable (may
		 * use prod-like) Transaction Rollback Yes (by default) No (unless manually
		 * configured) Use Case Repository/unit testing Integration/system testing
		 * 
		 * 🛠️ When to Use What? ✅ Use @DataJpaTest when: You're testing only repository
		 * logic. You want fast tests with rollback and isolation.
		 * 
		 * ✅ Use @SpringBootTest when: You're testing controller/service/repository
		 * integration. You want a realistic test environment. You’re using
		 * Testcontainers, WireMock, etc.
		 */
	}

}
