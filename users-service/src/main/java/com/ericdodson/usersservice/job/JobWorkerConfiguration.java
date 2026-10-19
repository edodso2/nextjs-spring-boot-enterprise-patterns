package com.ericdodson.usersservice.job;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@ConditionalOnProperty(name = "worker.enabled", havingValue = "true")
public class JobWorkerConfiguration {

    /**
     * One thread per polling loop. As a Spring bean it is shut down with the context and
     * Actuator publishes its metrics (executor.active, executor.pool.size) automatically.
     */
    @Bean
    public ThreadPoolTaskExecutor jobWorkerExecutor(@Value("${worker.concurrency}") int concurrency) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(concurrency);
        executor.setMaxPoolSize(concurrency);
        executor.setThreadNamePrefix("job-worker-");
        // On shutdown, let in-flight jobs finish instead of interrupting them
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(15);
        return executor;
    }
}
