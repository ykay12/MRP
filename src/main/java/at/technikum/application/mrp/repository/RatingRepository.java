package at.technikum.application.mrp.repository;

import at.technikum.application.mrp.model.Rating;

import java.util.List;
import java.util.Optional;

public interface RatingRepository {
    Optional<Rating> findById(String id);
    List<Rating> findByMediaId(String mediaId);
    List<Rating> findByUserId(String userId);
    Optional<Rating> findByMediaIdAndUserId(String mediaId, String userId);
    List<Rating> findAll();
    Rating save(Rating rating);
    Rating update(Rating rating);
    void delete(String id);
}