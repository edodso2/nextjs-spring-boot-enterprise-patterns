package com.ericdodson.usersservice.user;

import com.ericdodson.usersservice.job.Job;
import com.ericdodson.usersservice.job.JobRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final JobRepository jobRepository;

    public UserService(UserRepository userRepository, JobRepository jobRepository) {
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
    }

    /**
     * Stage 2: the outbox pattern. The user and the welcome email job commit in one
     * transaction, so there is never a user without a job or a job without a user.
     * The email itself is sent later by a worker, outside the request.
     */
    @Transactional
    public User createUser(CreateUserRequest request) {
        // Gives the user a specific message; the unique constraint still guards against races
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException(request.email());
        }
        User user = userRepository.save(User.from(request));
        jobRepository.save(Job.welcomeEmail(user.getId()));
        return user;
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }
}
