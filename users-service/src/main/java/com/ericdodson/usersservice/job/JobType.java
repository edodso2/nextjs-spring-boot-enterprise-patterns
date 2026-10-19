package com.ericdodson.usersservice.job;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum JobType {
    WELCOME_EMAIL("Job to send welcome email for a user");

    private final String description;
}
