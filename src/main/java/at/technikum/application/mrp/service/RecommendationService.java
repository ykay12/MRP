package at.technikum.application.mrp.service;

import at.technikum.application.mrp.model.Media;
import at.technikum.application.mrp.model.Rating;
import at.technikum.application.mrp.repository.MediaRepository;
import at.technikum.application.mrp.repository.RatingRepository;

import java.util.*;
import java.util.stream.Collectors;

public class RecommendationService {
    private final MediaRepository mediaRepository;
    private final RatingRepository ratingRepository;

    public RecommendationService(MediaRepository mediaRepository, RatingRepository ratingRepository) {
        this.mediaRepository = mediaRepository;
        this.ratingRepository = ratingRepository;
    }

    public List<Media> getRecommendations(String userId, int limit) {
        List<Rating> userRatings = ratingRepository.findByUserId(userId);

        if (userRatings.isEmpty()) {
            return mediaRepository.findAll().stream()
                    .sorted(Comparator.comparing(Media::getAverageRating).reversed())
                    .limit(limit)
                    .collect(Collectors.toList());
        }

        List<Rating> highRatings = userRatings.stream()
                .filter(r -> r.getStars() >= 4)
                .collect(Collectors.toList());

        Map<String, Integer> genreScores = new HashMap<>();
        for (Rating rating : highRatings) {
            Media media = mediaRepository.findById(rating.getMediaId()).orElse(null);
            if (media != null) {
                for (String genre : media.getGenres()) {
                    genreScores.put(genre, genreScores.getOrDefault(genre, 0) + rating.getStars());
                }
            }
        }

        Set<String> ratedMediaIds = userRatings.stream()
                .map(Rating::getMediaId)
                .collect(Collectors.toSet());

        List<Media> allMedia = mediaRepository.findAll();

        List<Media> recommendations = allMedia.stream()
                .filter(m -> !ratedMediaIds.contains(m.getId()))
                .map(m -> {
                    int score = 0;
                    for (String genre : m.getGenres()) {
                        score += genreScores.getOrDefault(genre, 0);
                    }
                    return new ScoredMedia(m, score);
                })
                .sorted(Comparator.comparing(ScoredMedia::getScore).reversed()
                        .thenComparing(sm -> sm.getMedia().getAverageRating(), Comparator.reverseOrder()))
                .map(ScoredMedia::getMedia)
                .limit(limit)
                .collect(Collectors.toList());

        return recommendations;
    }

    private static class ScoredMedia {
        private final Media media;
        private final int score;

        public ScoredMedia(Media media, int score) {
            this.media = media;
            this.score = score;
        }

        public Media getMedia() {
            return media;
        }

        public int getScore() {
            return score;
        }
    }
}