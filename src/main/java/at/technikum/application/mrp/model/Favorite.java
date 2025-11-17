package at.technikum.application.mrp.model;

public class Favorite {
    private String id;
    private String userId;
    private String mediaId;

    public Favorite() {
    }

    public Favorite(String id, String userId, String mediaId) {
        this.id = id;
        this.userId = userId;
        this.mediaId = mediaId;
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

    public String getMediaId() {
        return mediaId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }
}