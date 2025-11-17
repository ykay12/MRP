package at.technikum.application.mrp.repository;

import at.technikum.application.mrp.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    Optional<User> findById(String id);
    Optional<User> findByUsername(String username);
    Optional<User> findByToken(String token);
    List<User> findAll();
    User save(User user);
    User update(User user);
    void delete(String id);
    List<User> findTopByRatingCount(int limit);
}