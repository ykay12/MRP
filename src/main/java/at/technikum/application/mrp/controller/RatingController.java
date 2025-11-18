package at.technikum.application.mrp.controller;

import at.technikum.application.common.Controller;
import at.technikum.application.mrp.dto.RatingRequest;
import at.technikum.application.mrp.middleware.RequestContext;
import at.technikum.application.mrp.model.Rating;
import at.technikum.application.mrp.service.RatingService;
import at.technikum.server.http.Method;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;
import at.technikum.server.http.Status;

import java.util.List;

public class RatingController extends Controller {
    private final RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @Override
    public Response handle(Request request) {
        String path = request.getPath();
        String method = request.getMethod();

        if (method.equals(Method.POST.getVerb())) {
            if (path.equals("/api/ratings")) {
                return createRating(request);
            }
            if (path.matches("/api/ratings/[^/]+/like")) {
                String id = extractId(path);
                return likeRating(id);
            }
            if (path.matches("/api/ratings/[^/]+/confirm")) {
                String id = extractId(path);
                return confirmRating(id, request);
            }
        }

        if (method.equals(Method.GET.getVerb())) {
            if (path.matches("/api/ratings/media/[^/]+")) {
                String mediaId = extractMediaId(path);
                return getRatingsByMedia(mediaId);
            }
            if (path.matches("/api/ratings/user/[^/]+")) {
                String userId = extractUserId(path);
                return getRatingsByUser(userId);
            }
        }

        if (method.equals(Method.PUT.getVerb()) && path.matches("/api/ratings/[^/]+")) {
            String id = extractId(path);
            return updateRating(id, request);
        }

        if (method.equals(Method.DELETE.getVerb()) && path.matches("/api/ratings/[^/]+")) {
            String id = extractId(path);
            return deleteRating(id, request);
        }

        return status(Status.NOT_FOUND);
    }

    private Response createRating(Request request) {
        String userId = RequestContext.getCurrentUserId();

        // Debug logging
        // System.out.println("DEBUG RatingController.createRating: userId from context = " + userId);
        // System.out.println("DEBUG RatingController.createRating: current user = " + RequestContext.getCurrentUser());

        if (userId == null || userId.isEmpty()) {
            System.err.println("ERROR: userId is null or empty in RatingController!");
            return text("Unauthorized: User ID not found", Status.UNAUTHORIZED);
        }

        RatingRequest req = toObject(request.getBody(), RatingRequest.class);
        System.out.println("DEBUG RatingController.createRating: Creating rating for mediaId=" + req.getMediaId() + ", stars=" + req.getStars());

        Rating rating = ratingService.createRating(req.getMediaId(), userId, req.getStars(), req.getComment());
        return json(rating, Status.CREATED);
    }

    private Response getRatingsByMedia(String mediaId) {
        List<Rating> ratings = ratingService.getRatingsByMediaId(mediaId);
        return json(ratings, Status.OK);
    }

    private Response getRatingsByUser(String userId) {
        List<Rating> ratings = ratingService.getRatingsByUserId(userId);
        return json(ratings, Status.OK);
    }

    private Response updateRating(String id, Request request) {
        String userId = RequestContext.getCurrentUserId();

        if (userId == null || userId.isEmpty()) {
            return text("Unauthorized: User ID not found", Status.UNAUTHORIZED);
        }

        RatingRequest req = toObject(request.getBody(), RatingRequest.class);
        Rating rating = ratingService.updateRating(id, userId, req.getStars(), req.getComment());
        return json(rating, Status.OK);
    }

    private Response deleteRating(String id, Request request) {
        String userId = RequestContext.getCurrentUserId();

        if (userId == null || userId.isEmpty()) {
            return text("Unauthorized: User ID not found", Status.UNAUTHORIZED);
        }

        ratingService.deleteRating(id, userId);
        return status(Status.OK);
    }

    private Response confirmRating(String id, Request request) {
        String userId = RequestContext.getCurrentUserId();

        if (userId == null || userId.isEmpty()) {
            return text("Unauthorized: User ID not found", Status.UNAUTHORIZED);
        }

        Rating rating = ratingService.confirmRating(id, userId);
        return json(rating, Status.OK);
    }

    private Response likeRating(String id) {
        ratingService.likeRating(id);
        return status(Status.OK);
    }

    private String extractId(String path) {
        String[] parts = path.split("/");
        return parts[3];
    }

    private String extractMediaId(String path) {
        String[] parts = path.split("/");
        return parts[4];
    }

    private String extractUserId(String path) {
        String[] parts = path.split("/");
        return parts[4];
    }
}