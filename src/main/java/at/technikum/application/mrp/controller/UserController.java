package at.technikum.application.mrp.controller;

import at.technikum.application.common.Controller;
import at.technikum.application.mrp.dto.LoginRequest;
import at.technikum.application.mrp.dto.RegisterRequest;
import at.technikum.application.mrp.dto.TokenResponse;
import at.technikum.application.mrp.dto.UserProfileResponse;
import at.technikum.application.mrp.dto.UserProfileUpdateRequest;
import at.technikum.application.mrp.middleware.RequestContext;
import at.technikum.application.mrp.model.User;
import at.technikum.application.mrp.service.RatingService;
import at.technikum.application.mrp.service.UserService;
import at.technikum.server.http.Method;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;
import at.technikum.server.http.Status;

import java.util.List;

public class UserController extends Controller {
    private final UserService userService;
    private final RatingService ratingService;

    public UserController(UserService userService, RatingService ratingService) {
        this.userService = userService;
        this.ratingService = ratingService;
    }

    @Override
    public Response handle(Request request) {
        String path = request.getPath();
        String method = request.getMethod();

        if (method.equals(Method.POST.getVerb())) {
            if (path.equals("/api/users/register")) {
                return register(request);
            }
            if (path.equals("/api/users/login")) {
                return login(request);
            }
        }

        if (method.equals(Method.GET.getVerb())) {
            if (path.matches("/api/users/[^/]+/profile")) {
                String username = extractUsername(path);
                return getProfile(username);
            }
            if (path.equals("/api/users/leaderboard")) {
                return getLeaderboard();
            }
        }

        if (method.equals(Method.PUT.getVerb())) {
            if (path.matches("/api/users/[^/]+/profile")) {
                String userId = extractUserId(path);
                return updateProfile(userId, request);
            }
        }

        return status(Status.NOT_FOUND);
    }

    private Response register(Request request) {
        RegisterRequest req = toObject(request.getBody(), RegisterRequest.class);
        User user = userService.register(req.getUsername(), req.getPassword());
        return json(user, Status.CREATED);
    }

    private Response login(Request request) {
        LoginRequest req = toObject(request.getBody(), LoginRequest.class);
        String token = userService.login(req.getUsername(), req.getPassword());
        TokenResponse response = new TokenResponse(token);
        return json(response, Status.OK);
    }

    private Response getProfile(String username) {
        User user = userService.getUserByUsername(username);

        List<at.technikum.application.mrp.model.Rating> ratings = ratingService.getRatingsByUserId(user.getId());

        int totalRatings = ratings.size();
        double averageScore = ratings.stream()
                .mapToInt(at.technikum.application.mrp.model.Rating::getStars)
                .average()
                .orElse(0.0);

        UserProfileResponse profile = new UserProfileResponse();
        profile.setUsername(user.getUsername());
        profile.setTotalRatings(totalRatings);
        profile.setAverageScore(averageScore);
        profile.setFavoriteGenre(user.getFavoriteGenre());

        return json(profile, Status.OK);
    }

    private Response updateProfile(String userId, Request request) {
        String authenticatedUserId = RequestContext.getCurrentUserId();

        if (authenticatedUserId == null || !authenticatedUserId.equals(userId)) {
            return text("Forbidden: You can only update your own profile", Status.UNAUTHORIZED);
        }

        UserProfileUpdateRequest updateRequest = toObject(request.getBody(), UserProfileUpdateRequest.class);

        User user = userService.getUserById(userId);

        if (updateRequest.getFavoriteGenre() != null) {
            user.setFavoriteGenre(updateRequest.getFavoriteGenre());
        }

        User updatedUser = userService.updateProfile(user);

        UserProfileResponse profile = new UserProfileResponse();
        profile.setUsername(updatedUser.getUsername());
        profile.setTotalRatings(updatedUser.getTotalRatings());
        profile.setAverageScore(updatedUser.getAverageScore());
        profile.setFavoriteGenre(updatedUser.getFavoriteGenre());

        return json(profile, Status.OK);
    }

    private Response getLeaderboard() {
        List<User> topUsers = userService.getLeaderboard(10);
        return json(topUsers, Status.OK);
    }

    private String extractUsername(String path) {
        String[] parts = path.split("/");
        return parts[3];
    }

    private String extractUserId(String path) {
        String[] parts = path.split("/");
        return parts[3];
    }
}