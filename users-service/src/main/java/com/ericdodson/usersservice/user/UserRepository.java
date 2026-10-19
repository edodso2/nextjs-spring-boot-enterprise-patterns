package com.ericdodson.usersservice.user;

import org.springframework.data.repository.ListCrudRepository;

public interface UserRepository extends ListCrudRepository<User, Long> {

    boolean existsByEmail(String email);
}
