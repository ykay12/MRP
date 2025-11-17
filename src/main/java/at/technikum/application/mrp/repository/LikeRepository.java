package at.technikum.application.mrp.repository;

import at.technikum.application.mrp.model.Like;

import java.util.List;
import java.util.Optional;

public interface LikeRepository {
    Optional<Like> findById(String id);
    List<Like> findByRatingId(String ratingId);
    Optional<Like> findByUserIdAndRatingId(String userId, String ratingId);
    Like save(Like like);
    void delete(String id);
    int countByRatingId(String ratingId);
}