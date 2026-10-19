package com.ericdodson.usersservice.job;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;

import java.util.Optional;

public interface JobRepository extends ListCrudRepository<Job, Long> {

    /**
     * Locks the oldest pending job for the current transaction. SKIP LOCKED makes
     * concurrent workers pass over rows another worker already holds instead of
     * waiting on them, so each job is claimed by exactly one worker at a time.
     */
    @Query("""
            SELECT * FROM jobs
            WHERE status = 'PENDING'
            ORDER BY id
            LIMIT 1
            FOR UPDATE SKIP LOCKED
            """)
    Optional<Job> claimNextPending();
}
