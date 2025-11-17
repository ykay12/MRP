package at.technikum.application.mrp.middleware;

import at.technikum.application.mrp.exception.UnauthorizedException;
import at.technikum.application.mrp.model.User;
import at.technikum.application.mrp.service.UserService;
import at.technikum.server.http.Request;

import java.util.Arrays;
import java.util.List;

public class AuthMiddleware {
    private final UserService userService;

    private static final List<String> PUBLIC_PATHS = Arrays.asList(
            "/api/users/register",
            "/api/users/login"
    );

    public AuthMiddleware(UserService userService) {
        this.userService = userService;
    }

    public User authenticate(Request request) {
        // Check if path requires authentication
        String path = request.getPath();

        for (String publicPath : PUBLIC_PATHS) {
            if (path.equals(publicPath)) {
                return null; // Public endpoint, no auth needed
            }
        }

        // Extract token from Authorization header
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || authHeader.isEmpty()) {
            throw new UnauthorizedException("Missing Authorization header");
        }

        // Expected format: "Bearer <token>"
        if (!authHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException("Invalid Authorization header format");
        }

        String token = authHeader.substring(7); // Remove "Bearer " prefix

        if (token.isEmpty()) {
            throw new UnauthorizedException("Missing token");
        }

        // Validate token and get user
        User user = userService.getUserByToken(token);

        if (user == null) {
            throw new UnauthorizedException("Invalid token");
        }

        return user;
    }

    public boolean isPublicPath(String path) {
        return PUBLIC_PATHS.stream().anyMatch(path::equals);
    }
}