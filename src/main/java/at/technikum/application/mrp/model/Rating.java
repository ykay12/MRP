package at.technikum.application.mrp.model;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Rating {
    private String id;
    private String mediaId;
    private String userId;
    private int stars;
    private String comment;
    private LocalDateTime timestamp;
    private boolean confirmed;
    private int likes;

    public Rating() {
    }

    public Rating(String id, String mediaId, String userId, int stars, String comment) {
        this.id = id;
        this.mediaId = mediaId;
        this.userId = userId;
        this.stars = stars;
        this.comment = comment;
        this.timestamp = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        this.confirmed = false;
        this.likes = 0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMediaId() {
        return mediaId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }
}