package com.ericdodson.emailservice.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Fake third-party email provider. Nothing is delivered; every send is logged
 * so you can count sends and duplicates with `docker compose logs email-service`.
 */
@RestController
@RequestMapping("/api/emails")
public class EmailController {

    private static final Logger log = LoggerFactory.getLogger(EmailController.class);

    private static final long MIN_LATENCY_MS = 2000;
    private static final long MAX_LATENCY_MS = 4000;
    private static final double FAILURE_RATE = 0.2;

    @PostMapping
    public ResponseEntity<Map<String, String>> send(@RequestBody SendEmailRequest request) throws InterruptedException {
        Thread.sleep(ThreadLocalRandom.current().nextLong(MIN_LATENCY_MS, MAX_LATENCY_MS + 1));

        if (ThreadLocalRandom.current().nextDouble() < FAILURE_RATE) {
            log.warn("FAILED to={}", request.to());
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Simulated provider failure");
        }

        String messageId = UUID.randomUUID().toString();
        log.info("SENT messageId={} to={} subject=\"{}\"", messageId, request.to(), request.subject());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(Map.of("messageId", messageId));
    }

    public record SendEmailRequest(String to, String subject, String body) {
    }
}
