package at.technikum.application.mrp.controller;

import at.technikum.application.common.Controller;
import at.technikum.application.mrp.dto.FavoriteRequest;
import at.technikum.application.mrp.model.Media;
import at.technikum.application.mrp.service.FavoriteService;
import at.technikum.server.http.Method;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;
import at.technikum.server.http.Status;

import java.util.List;

public class FavoriteController extends Controller {
    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @Override
    public Response handle(Request request) {
        String path = request.getPath();
        String method = request.getMethod();

        if (method.equals(Method.POST.getVerb()) && path.equals("/api/favorites")) {
            return addFavorite(request);
        }

        if (method.equals(Method.DELETE.getVerb()) && path.matches("/api/favorites/[^/]+")) {
            String mediaId = extractMediaId(path);
            return removeFavorite(mediaId, request);
        }

        if (method.equals(Method.GET.getVerb()) && path.equals("/api/favorites")) {
            return getFavorites(request);
        }

        return status(Status.NOT_FOUND);
    }

    private Response addFavorite(Request request) {
        String userId = getUserIdFromAuth(request);
        FavoriteRequest req = toObject(request.getBody(), FavoriteRequest.class);
        favoriteService.addFavorite(userId, req.getMediaId());
        return status(Status.CREATED);
    }

    private Response removeFavorite(String mediaId, Request request) {
        String userId = getUserIdFromAuth(request);
        favoriteService.removeFavorite(userId, mediaId);
        return status(Status.OK);
    }

    private Response getFavorites(Request request) {
        String userId = getUserIdFromAuth(request);
        List<Media> favorites = favoriteService.getUserFavorites(userId);
        return json(favorites, Status.OK);
    }

    private String extractMediaId(String path) {
        String[] parts = path.split("/");
        return parts[3];
    }

    private String getUserIdFromAuth(Request request) {
        return "user-id-placeholder";
    }
}