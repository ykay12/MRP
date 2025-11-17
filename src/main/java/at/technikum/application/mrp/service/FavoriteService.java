package at.technikum.application.mrp.service;

import at.technikum.application.mrp.exception.AlreadyExistsException;
import at.technikum.application.mrp.model.Favorite;
import at.technikum.application.mrp.model.Media;
import at.technikum.application.mrp.repository.FavoriteRepository;
import at.technikum.application.todo.exception.EntityNotFoundException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class FavoriteService {
    private final FavoriteRepository favoriteRepository;
    private final MediaService mediaService;

    public FavoriteService(FavoriteRepository favoriteRepository, MediaService mediaService) {
        this.favoriteRepository = favoriteRepository;
        this.mediaService = mediaService;
    }

    public Favorite addFavorite(String userId, String mediaId) {
        if (favoriteRepository.findByUserIdAndMediaId(userId, mediaId).isPresent()) {
            throw new AlreadyExistsException("Media is already in favorites");
        }

        Favorite favorite = new Favorite();
        favorite.setId(UUID.randomUUID().toString());
        favorite.setUserId(userId);
        favorite.setMediaId(mediaId);

        return favoriteRepository.save(favorite);
    }

    public void removeFavorite(String userId, String mediaId) {
        Favorite favorite = favoriteRepository.findByUserIdAndMediaId(userId, mediaId)
                .orElseThrow(() -> new EntityNotFoundException("Favorite not found"));

        favoriteRepository.delete(favorite.getId());
    }

    public List<Media> getUserFavorites(String userId) {
        List<Favorite> favorites = favoriteRepository.findByUserId(userId);

        return favorites.stream()
                .map(fav -> mediaService.getMediaById(fav.getMediaId()))
                .collect(Collectors.toList());
    }

    public boolean isFavorite(String userId, String mediaId) {
        return favoriteRepository.findByUserIdAndMediaId(userId, mediaId).isPresent();
    }
}