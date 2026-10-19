package com.ericdodson.usersservice.job;

import com.ericdodson.usersservice.email.EmailClient;
import com.ericdodson.usersservice.user.User;
import com.ericdodson.usersservice.user.UserNotFoundException;
import com.ericdodson.usersservice.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class JobProcessor {

    private static final Logger log = LoggerFactory.getLogger(JobProcessor.class);

    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final EmailClient emailClient;

    public JobProcessor(JobRepository jobRepository, UserRepository userRepository, EmailClient emailClient) {
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
        this.emailClient = emailClient;
    }

    /**
     * Claims one pending job, sends the email and records the result, all in one transaction.
     * The row stays locked while the email is sent. If the worker dies mid-send, the transaction
     * rolls back and the job is PENDING again for the next worker.
     *
     * @return false when there was no pending job to claim
     */
    @Transactional
    public boolean processNextJob() {
        Job job = jobRepository.claimNextPending().orElse(null);
        if (job == null) {
            return false;
        }

        try {
            User user = userRepository.findById(job.getUserId())
                    .orElseThrow(() -> new UserNotFoundException(job.getUserId()));
            emailClient.sendWelcomeEmail(user);
            job.setStatus(JobStatus.DONE);
            log.info("Job {} DONE for user {}", job.getId(), job.getUserId());
        } catch (Exception e) {
            // Stage 2 has no retries: one failure is permanent
            job.setStatus(JobStatus.FAILED);
            log.warn("Job {} FAILED for user {}: {}", job.getId(), job.getUserId(), e.getMessage());
        }

        jobRepository.save(job);
        return true;
    }
}
