package com.ericdodson.usersservice.email;

import com.ericdodson.usersservice.user.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;

@Component
public class EmailClient {

    private final RestClient restClient;

    public EmailClient(
            RestClient.Builder builder,
            @Value("${email-service.url}") String baseUrl,
            @Value("${email-service.connect-timeout}") Duration connectTimeout,
            @Value("${email-service.read-timeout}") Duration readTimeout) {
        // Explicit timeouts: without them a hung provider holds the request thread indefinitely
        HttpClient httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(connectTimeout)
                .build();
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(readTimeout);

        this.restClient = builder
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .build();
    }

    public void sendWelcomeEmail(User user) {
        SendEmailRequest request = new SendEmailRequest(
                user.getEmail(),
                "Welcome, " + user.getFirstName() + "!",
                "Thanks for signing up, " + user.getFirstName() + ".");

        restClient.post()
                .uri("/api/emails")
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }

    private record SendEmailRequest(String to, String subject, String body) {
    }
}
