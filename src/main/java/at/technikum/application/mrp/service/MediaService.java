package at.technikum.application.mrp.service;

import at.technikum.application.mrp.exception.ForbiddenException;
import at.technikum.application.mrp.model.Media;
import at.technikum.application.mrp.model.MediaType;
import at.technikum.application.mrp.repository.MediaRepository;
import at.technikum.application.todo.exception.EntityNotFoundException;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class MediaService {
    private final MediaRepository mediaRepository;

    public MediaService(MediaRepository mediaRepository) {
        this.mediaRepository = mediaRepository;
    }

    public Media createMedia(Media media, String creatorId) {
        media.setId(UUID.randomUUID().toString());
        media.setCreatorId(creatorId);
        media.setAverageRating(0.0);
        media.setRatingCount(0);
        return mediaRepository.save(media);
    }

    public Media getMediaById(String id) {
        return mediaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Media not found"));
    }

    public List<Media> getAllMedia() {
        return mediaRepository.findAll();
    }

    public Media updateMedia(String id, Media updatedMedia, String userId) {
        Media media = getMediaById(id);

        if (!media.getCreatorId().equals(userId)) {
            throw new ForbiddenException("You can only edit your own media");
        }

        media.setTitle(updatedMedia.getTitle());
        media.setDescription(updatedMedia.getDescription());
        media.setMediaType(updatedMedia.getMediaType());
        media.setReleaseYear(updatedMedia.getReleaseYear());
        media.setGenres(updatedMedia.getGenres());
        media.setAgeRestriction(updatedMedia.getAgeRestriction());

        return mediaRepository.update(media);
    }

    public void deleteMedia(String id, String userId) {
        Media media = getMediaById(id);

        if (!media.getCreatorId().equals(userId)) {
            throw new ForbiddenException("You can only delete your own media");
        }

        mediaRepository.delete(id);
    }

    public List<Media> searchByTitle(String title) {
        return mediaRepository.findByTitle(title);
    }

    public List<Media> filterMedia(String genre, MediaType mediaType, Integer year, Integer ageRestriction, Double minRating) {
        List<Media> results = mediaRepository.findAll();

        if (genre != null && !genre.isEmpty()) {
            results = results.stream()
                    .filter(m -> m.getGenres().contains(genre))
                    .collect(Collectors.toList());
        }

        if (mediaType != null) {
            results = results.stream()
                    .filter(m -> m.getMediaType() == mediaType)
                    .collect(Collectors.toList());
        }

        if (year != null) {
            results = results.stream()
                    .filter(m -> m.getReleaseYear() == year)
                    .collect(Collectors.toList());
        }

        if (ageRestriction != null) {
            results = results.stream()
                    .filter(m -> m.getAgeRestriction() <= ageRestriction)
                    .collect(Collectors.toList());
        }

        if (minRating != null) {
            results = results.stream()
                    .filter(m -> m.getAverageRating() >= minRating)
                    .collect(Collectors.toList());
        }

        return results;
    }

    public List<Media> sortMedia(List<Media> mediaList, String sortBy) {
        if (sortBy == null) {
            return mediaList;
        }

        return switch (sortBy.toLowerCase()) {
            case "title" -> mediaList.stream()
                    .sorted(Comparator.comparing(Media::getTitle))
                    .collect(Collectors.toList());
            case "year" -> mediaList.stream()
                    .sorted(Comparator.comparing(Media::getReleaseYear))
                    .collect(Collectors.toList());
            case "rating" -> mediaList.stream()
                    .sorted(Comparator.comparing(Media::getAverageRating).reversed())
                    .collect(Collectors.toList());
            default -> mediaList;
        };
    }

    public void updateAverageRating(String mediaId, double newAverage, int ratingCount) {
        Media media = getMediaById(mediaId);
        media.setAverageRating(newAverage);
        media.setRatingCount(ratingCount);
        mediaRepository.update(media);
    }
}