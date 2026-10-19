package com.ericdodson.usersservice.user;

/**
 * What a client may send to create a user. Server-owned fields (id, timestamps)
 * aren't here, so a client can't set them.
 */
public record CreateUserRequest(String email, String firstName, String lastName) {
}
