package com.ericdodson.usersservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.ericdodson.usersservice.email.EmailClient;
import com.ericdodson.usersservice.user.CreateUserRequest;
import com.ericdodson.usersservice.user.User;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class UsersApplicationTests {

	@Container
	@ServiceConnection
	static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:18-alpine");

	@Autowired
	private TestRestTemplate restTemplate;

	@MockitoBean
	private EmailClient emailClient;

	@Test
	void createsUserAndRejectsDuplicateEmail() {
		CreateUserRequest request = new CreateUserRequest("test.user@example.com", "Test", "User");

		ResponseEntity<User> created = restTemplate.postForEntity("/api/users", request, User.class);
		assertThat(created.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(created.getBody().getId()).isNotNull();
		assertThat(created.getBody().getCreatedAt()).isNotNull();

		ResponseEntity<String> duplicate = restTemplate.postForEntity("/api/users", request, String.class);
		assertThat(duplicate.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
		assertThat(duplicate.getBody()).contains("Email already exists");
		verify(emailClient, times(1)).sendWelcomeEmail(any(User.class));
	}

	@Test
	void returnsNotFoundForUnknownUser() {
		ResponseEntity<String> response = restTemplate.getForEntity("/api/users/999999", String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	void exposesHealthEndpoint() {
		ResponseEntity<String> health = restTemplate.getForEntity("/actuator/health", String.class);
		assertThat(health.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

}
