package com.ericdodson.usersservice.job;

import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * Polls the jobs table and processes jobs on a fixed number of threads.
 * Only runs in a process started with worker.enabled=true, so the API and the
 * worker can be stopped, killed and scaled independently.
 */
@Component
@ConditionalOnProperty(name = "worker.enabled", havingValue = "true")
public class JobWorker {

    private static final Logger log = LoggerFactory.getLogger(JobWorker.class);

    private final JobProcessor jobProcessor;
    private final ThreadPoolTaskExecutor jobWorkerExecutor;
    private final Duration pollInterval;
    private volatile boolean running = true;

    public JobWorker(
            JobProcessor jobProcessor,
            ThreadPoolTaskExecutor jobWorkerExecutor,
            @Value("${worker.poll-interval}") Duration pollInterval) {
        this.jobProcessor = jobProcessor;
        this.jobWorkerExecutor = jobWorkerExecutor;
        this.pollInterval = pollInterval;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void start() {
        int threads = jobWorkerExecutor.getCorePoolSize();
        for (int i = 0; i < threads; i++) {
            jobWorkerExecutor.execute(this::pollLoop);
        }
        log.info("Job worker started with {} threads", threads);
    }

    private void pollLoop() {
        while (running) {
            try {
                boolean processed = jobProcessor.processNextJob();
                if (!processed) {
                    // Queue is empty; wait before polling again instead of hammering the database
                    Thread.sleep(pollInterval);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            } catch (Exception e) {
                // For example the database is unreachable; back off and keep the thread alive
                log.error("Job worker poll failed", e);
                try {
                    Thread.sleep(pollInterval);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }

    /**
     * The loops never finish on their own, so stop them first, then let the executor wait
     * for in-flight jobs. This runs before the database pool closes, because Spring destroys
     * this bean before the beans it depends on.
     */
    @PreDestroy
    public void stop() {
        running = false;
        jobWorkerExecutor.shutdown();
        log.info("Job worker stopped");
    }
}
