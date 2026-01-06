package at.technikum.application.mrp.service;

import at.technikum.application.mrp.exception.ForbiddenException;
import at.technikum.application.mrp.exception.InvalidRatingException;
import at.technikum.application.mrp.model.Rating;
import at.technikum.application.mrp.repository.RatingRepository;
import at.technikum.application.todo.exception.EntityNotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class RatingService {
    private final RatingRepository ratingRepository;
    private final MediaService mediaService;

    public RatingService(RatingRepository ratingRepository, MediaService mediaService) {
        this.ratingRepository = ratingRepository;
        this.mediaService = mediaService;
    }

    public Rating createRating(String mediaId, String userId, int stars, String comment) {
        if (stars < 1 || stars > 5) {
            throw new InvalidRatingException("Stars must be between 1 and 5");
        }

        if (ratingRepository.findByMediaIdAndUserId(mediaId, userId).isPresent()) {
            throw new InvalidRatingException("You have already rated this media");
        }

        Rating rating = new Rating();
        rating.setId(UUID.randomUUID().toString());
        rating.setMediaId(mediaId);
        rating.setUserId(userId);
        rating.setStars(stars);
        rating.setComment(comment);
        rating.setCreatedAt(LocalDateTime.now());
        rating.setConfirmed(false);
        rating.setLikes(0);

        Rating savedRating = ratingRepository.save(rating);
        updateMediaAverageRating(mediaId);

        return savedRating;
    }

    public Rating getRatingById(String id) {
        return ratingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Rating not found"));
    }

    public List<Rating> getRatingsByMediaId(String mediaId) {
        return ratingRepository.findByMediaId(mediaId);
    }

    public List<Rating> getRatingsByUserId(String userId) {
        return ratingRepository.findByUserId(userId);
    }

    public Rating updateRating(String ratingId, String userId, int stars, String comment) {
        if (stars < 1 || stars > 5) {
            throw new InvalidRatingException("Stars must be between 1 and 5");
        }

        Rating rating = getRatingById(ratingId);

        if (!rating.getUserId().equals(userId)) {
            throw new ForbiddenException("You can only edit your own ratings");
        }

        rating.setStars(stars);
        rating.setComment(comment);
        rating.setConfirmed(false);

        Rating updatedRating = ratingRepository.update(rating);
        updateMediaAverageRating(rating.getMediaId());

        return updatedRating;
    }

    public void deleteRating(String ratingId, String userId) {
        Rating rating = getRatingById(ratingId);

        if (!rating.getUserId().equals(userId)) {
            throw new ForbiddenException("You can only delete your own ratings");
        }

        String mediaId = rating.getMediaId();
        ratingRepository.delete(ratingId);
        updateMediaAverageRating(mediaId);
    }

    public Rating confirmRating(String ratingId, String userId) {
        Rating rating = getRatingById(ratingId);

        if (!rating.getUserId().equals(userId)) {
            throw new ForbiddenException("You can only confirm your own ratings");
        }

        rating.setConfirmed(true);
        return ratingRepository.update(rating);
    }

    public void likeRating(String ratingId) {
        Rating rating = getRatingById(ratingId);
        rating.setLikes(rating.getLikes() + 1);
        ratingRepository.update(rating);
    }
    // average von sql ausrechnen lassen ?
    private void updateMediaAverageRating(String mediaId) {
        List<Rating> ratings = ratingRepository.findByMediaId(mediaId);

        if (ratings.isEmpty()) {
            mediaService.updateAverageRating(mediaId, 0.0, 0);
            return;
        }

        double average = ratings.stream()
                .mapToInt(Rating::getStars)
                .average()
                .orElse(0.0);

        mediaService.updateAverageRating(mediaId, average, ratings.size());
    }
}