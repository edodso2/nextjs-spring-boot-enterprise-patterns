package com.ericdodson.usersservice.user;

import org.springframework.stereotype.Repository;
import jakarta.annotation.PostConstruct;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class UserRepository {

    private final Map<Long, User> users = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    @PostConstruct
    private void initializeDefaultUsers() {
        LocalDateTime now = LocalDateTime.now();

        // Default User 1
        User user1 = new User(
                1L,
                "john.doe@example.com",
                "John",
                "Doe",
                now,
                now
        );

        // Default User 2
        User user2 = new User(
                2L,
                "jane.smith@example.com",
                "Jane",
                "Smith",
                now,
                now
        );

        // Default User 3
        User user3 = new User(
                3L,
                "alice.johnson@example.com",
                "Alice",
                "Johnson",
                now,
                now
        );

        users.put(1L, user1);
        users.put(2L, user2);
        users.put(3L, user3);

        // Set the counter to start after the default users
        idCounter.set(4);
    }

    // CREATE
    public User save(User user) {
        if (user.getId() == null) {
            // New user - generate ID
            user.setId(idCounter.getAndIncrement());
        }
        users.put(user.getId(), user);
        return user;
    }

    // READ - find by ID
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    // READ - find all users
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    // UPDATE (same as save - will overwrite existing)
    public User update(User user) {
        if (user.getId() == null || !users.containsKey(user.getId())) {
            throw new IllegalArgumentException("User does not exist");
        }
        users.put(user.getId(), user);
        return user;
    }

    // DELETE
    public void deleteById(Long id) {
        users.remove(id);
    }

    public boolean existsByEmail(String email) {
        return users.values().stream()
                .anyMatch(user -> user.getEmail().equals(email));
    }
}