package com.fixmycity.service;

import com.fixmycity.model.User;
import com.fixmycity.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public User register(User user) {
        if (user.getUsername() != null && userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        // Simple validation
        if (user.getPassword() == null || user.getPassword().length() < 4) {
            throw new RuntimeException("Password must be at least 4 characters long.");
        }

        if (user.getName() == null || user.getName().trim().isEmpty()) {
            user.setName(user.getUsername());
        }

        user.setPoints(0);
        return userRepository.save(user);
    }

    public Optional<User> login(String username, String password) {
        return userRepository.findByUsername(username)
                .filter(u -> u.getPassword().equals(password));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<User> getLeaderboard() {
        // Return top 10 users sorted by points descending
        return userRepository.findAll().stream()
                .sorted((u1, u2) -> Integer.compare(u2.getPoints(), u1.getPoints()))
                .limit(10)
                .collect(Collectors.toList());
    }

    public User addPoints(Long userId, int points) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setPoints(user.getPoints() + points);
            return userRepository.save(user);
        }
        return null;
    }
}
