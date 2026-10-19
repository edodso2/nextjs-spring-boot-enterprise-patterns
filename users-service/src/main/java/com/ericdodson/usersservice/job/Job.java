package com.ericdodson.usersservice.job;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("jobs")
public class Job {

    @Id
    private Long id;
    private JobType type;
    private Long userId;
    private JobStatus status;
    @CreatedDate
    private Instant createdAt;

    public static Job welcomeEmail(Long userId) {
        var welcomeEmailJob = new Job();
        welcomeEmailJob.userId = userId;
        welcomeEmailJob.status = JobStatus.PENDING;
        welcomeEmailJob.type = JobType.WELCOME_EMAIL;
        return welcomeEmailJob;
    }
}
