package at.technikum.application.mrp.dto;

public class UserProfileUpdateRequest {
    private String favoriteGenre;

    public UserProfileUpdateRequest() {
    }

    public String getFavoriteGenre() {
        return favoriteGenre;
    }

    public void setFavoriteGenre(String favoriteGenre) {
        this.favoriteGenre = favoriteGenre;
    }
}