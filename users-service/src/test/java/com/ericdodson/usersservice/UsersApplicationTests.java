package com.ericdodson.usersservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.ericdodson.usersservice.user.User;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class UsersApplicationTests {

	@Container
	@ServiceConnection
	static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:18-alpine");

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	void createsUserAndRejectsDuplicateEmail() {
		User user = new User(null, "test.user@example.com", "Test", "User", null, null);

		ResponseEntity<User> created = restTemplate.postForEntity("/api/users", user, User.class);
		assertThat(created.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(created.getBody().getId()).isNotNull();
		assertThat(created.getBody().getCreatedAt()).isNotNull();

		ResponseEntity<String> duplicate = restTemplate.postForEntity("/api/users", user, String.class);
		assertThat(duplicate.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
	}

	@Test
	void exposesHealthEndpoint() {
		ResponseEntity<String> health = restTemplate.getForEntity("/actuator/health", String.class);
		assertThat(health.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

}
