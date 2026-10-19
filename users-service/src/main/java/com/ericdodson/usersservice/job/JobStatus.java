package com.ericdodson.usersservice.job;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum JobStatus {
    PENDING("Job is not started"),
    DONE("Job is completed"),
    FAILED("Job did not complete");

    private final String description;
}
