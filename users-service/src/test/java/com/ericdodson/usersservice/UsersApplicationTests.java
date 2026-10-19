package com.ericdodson.usersservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.RestClientException;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.ericdodson.usersservice.email.EmailClient;
import com.ericdodson.usersservice.job.JobProcessor;
import com.ericdodson.usersservice.job.JobRepository;
import com.ericdodson.usersservice.job.JobStatus;
import com.ericdodson.usersservice.job.JobType;
import com.ericdodson.usersservice.user.CreateUserRequest;
import com.ericdodson.usersservice.user.User;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class UsersApplicationTests {

	@Container
	@ServiceConnection
	static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:18-alpine");

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private JobRepository jobRepository;

	@Autowired
	private JobProcessor jobProcessor;

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

		// One welcome email job is queued for the new user, and no email is sent inside the request
		Long userId = created.getBody().getId();
		assertThat(jobRepository.findAll())
				.filteredOn(job -> job.getUserId().equals(userId))
				.singleElement()
				.satisfies(job -> {
					assertThat(job.getType()).isEqualTo(JobType.WELCOME_EMAIL);
					assertThat(job.getStatus()).isEqualTo(JobStatus.PENDING);
					assertThat(job.getCreatedAt()).isNotNull();
				});
		verifyNoInteractions(emailClient);
	}

	@Test
	void workerSendsEmailAndMarksJobDone() {
		User user = restTemplate.postForEntity("/api/users",
				new CreateUserRequest("worker.done@example.com", "Done", "User"), User.class).getBody();

		processAllPendingJobs();

		assertThat(jobStatusFor(user)).isEqualTo(JobStatus.DONE);
		verify(emailClient, times(1)).sendWelcomeEmail(argThat(u -> u.getId().equals(user.getId())));
	}

	@Test
	void workerMarksJobFailedWhenEmailFails() {
		User user = restTemplate.postForEntity("/api/users",
				new CreateUserRequest("worker.failed@example.com", "Failed", "User"), User.class).getBody();
		doThrow(new RestClientException("Simulated provider failure"))
				.when(emailClient).sendWelcomeEmail(argThat(u -> u.getId().equals(user.getId())));

		processAllPendingJobs();

		assertThat(jobStatusFor(user)).isEqualTo(JobStatus.FAILED);
	}

	// Other tests leave pending jobs behind, so drain the whole queue rather than one job
	private void processAllPendingJobs() {
		while (jobProcessor.processNextJob()) {
		}
	}

	private JobStatus jobStatusFor(User user) {
		return jobRepository.findAll().stream()
				.filter(job -> job.getUserId().equals(user.getId()))
				.findFirst()
				.orElseThrow()
				.getStatus();
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
