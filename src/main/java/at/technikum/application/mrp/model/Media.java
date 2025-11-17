package at.technikum.application.mrp.model;

import java.util.ArrayList;
import java.util.List;

public class Media {
    private String id;
    private String title;
    private String description;
    private MediaType mediaType;
    private int releaseYear;
    private List<String> genres;
    private int ageRestriction;
    private String creatorId;
    private double averageRating;
    private int ratingCount;

    public Media() {
        this.genres = new ArrayList<>();
    }

    public Media(String id, String title, String description, MediaType mediaType,
                 int releaseYear, List<String> genres, int ageRestriction, String creatorId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.mediaType = mediaType;
        this.releaseYear = releaseYear;
        this.genres = genres != null ? genres : new ArrayList<>();
        this.ageRestriction = ageRestriction;
        this.creatorId = creatorId;
        this.averageRating = 0.0;
        this.ratingCount = 0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    public void setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public int getAgeRestriction() {
        return ageRestriction;
    }

    public void setAgeRestriction(int ageRestriction) {
        this.ageRestriction = ageRestriction;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public int getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(int ratingCount) {
        this.ratingCount = ratingCount;
    }
}