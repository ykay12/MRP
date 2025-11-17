package at.technikum.application.mrp.model;

public class Like {
    private String id;
    private String userId;
    private String ratingId;

    public Like() {
    }

    public Like(String id, String userId, String ratingId) {
        this.id = id;
        this.userId = userId;
        this.ratingId = ratingId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRatingId() {
        return ratingId;
    }

    public void setRatingId(String ratingId) {
        this.ratingId = ratingId;
    }
}