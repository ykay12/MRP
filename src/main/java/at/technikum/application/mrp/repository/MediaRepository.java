package at.technikum.application.mrp.repository;

import at.technikum.application.mrp.model.Media;
import at.technikum.application.mrp.model.MediaType;

import java.util.List;
import java.util.Optional;

public interface MediaRepository {
    Optional<Media> findById(String id);
    List<Media> findAll();
    List<Media> findByTitle(String title);
    List<Media> findByGenre(String genre);
    List<Media> findByMediaType(MediaType mediaType);
    List<Media> findByReleaseYear(int year);
    List<Media> findByAgeRestriction(int maxAge);
    Media save(Media media);
    Media update(Media media);
    void delete(String id);
}