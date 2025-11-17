package at.technikum.application.mrp.service;

import at.technikum.application.mrp.exception.UnauthorizedException;
import at.technikum.application.mrp.exception.UserAlreadyExistsException;
import at.technikum.application.mrp.model.User;
import at.technikum.application.mrp.repository.UserRepository;
import at.technikum.application.todo.exception.EntityNotFoundException;

import java.util.List;
import java.util.UUID;

public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User register(String username, String password) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new UserAlreadyExistsException("Username already exists");
        }

        User user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setUsername(username);
        user.setPassword(password);
        user.setTotalRatings(0);
        user.setAverageScore(0.0);

        return userRepository.save(user);
    }

    public String login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

        if (!user.getPassword().equals(password)) {
            throw new UnauthorizedException("Invalid credentials");
        }

        String token = username + "-mrpToken";
        user.setToken(token);
        userRepository.update(user);

        return token;
    }

    public User getUserByToken(String token) {
        return userRepository.findByToken(token)
                .orElseThrow(() -> new UnauthorizedException("Invalid token"));
    }

    public User getUserById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    public User updateProfile(User user) {
        return userRepository.update(user);
    }

    public void updateUserStats(String userId) {
        User user = getUserById(userId);
        userRepository.update(user);
    }

    public List<User> getLeaderboard(int limit) {
        return userRepository.findTopByRatingCount(limit);
    }
}