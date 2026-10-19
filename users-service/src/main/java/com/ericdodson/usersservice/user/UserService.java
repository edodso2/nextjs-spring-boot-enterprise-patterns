package com.ericdodson.usersservice.user;

import com.ericdodson.usersservice.email.EmailClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final EmailClient emailClient;

    public UserService(UserRepository userRepository, EmailClient emailClient) {
        this.userRepository = userRepository;
        this.emailClient = emailClient;
    }

    /**
     * Stage 1: deliberately under-engineered. The user row commits on its own, then the
     * request waits on the email provider. A slow provider makes signup slow, and a failed
     * send returns 500 even though the user already exists.
     */
    public User createUser(CreateUserRequest request) {
        // Gives the user a specific message; the unique constraint still guards against races
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException(request.email());
        }
        User user = userRepository.save(User.from(request));
        emailClient.sendWelcomeEmail(user);
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
