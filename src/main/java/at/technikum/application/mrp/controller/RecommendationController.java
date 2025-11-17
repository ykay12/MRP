package at.technikum.application.mrp.controller;

import at.technikum.application.common.Controller;
import at.technikum.application.mrp.model.Media;
import at.technikum.application.mrp.service.RecommendationService;
import at.technikum.server.http.Method;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;
import at.technikum.server.http.Status;

import java.util.List;

public class RecommendationController extends Controller {
    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @Override
    public Response handle(Request request) {
        String path = request.getPath();
        String method = request.getMethod();

        if (method.equals(Method.GET.getVerb()) && path.equals("/api/recommendations")) {
            return getRecommendations(request);
        }

        return status(Status.NOT_FOUND);
    }

    private Response getRecommendations(Request request) {
        String userId = getUserIdFromAuth(request);
        List<Media> recommendations = recommendationService.getRecommendations(userId, 10);
        return json(recommendations, Status.OK);
    }

    private String getUserIdFromAuth(Request request) {
        return "user-id-placeholder";
    }
}