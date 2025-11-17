package at.technikum.application.mrp.repository;

import at.technikum.application.mrp.model.Favorite;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository {
    Optional<Favorite> findById(String id);
    List<Favorite> findByUserId(String userId);
    Optional<Favorite> findByUserIdAndMediaId(String userId, String mediaId);
    Favorite save(Favorite favorite);
    void delete(String id);
}