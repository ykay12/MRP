package at.technikum.application.mrp.controller;

import at.technikum.application.common.Controller;
import at.technikum.application.mrp.middleware.RequestContext;
import at.technikum.application.mrp.model.Media;
import at.technikum.application.mrp.model.MediaType;
import at.technikum.application.mrp.service.MediaService;
import at.technikum.server.http.Method;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;
import at.technikum.server.http.Status;

import java.util.List;

public class MediaController extends Controller {
    private final MediaService mediaService;

    public MediaController(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    @Override
    public Response handle(Request request) {
        String path = request.getPath();
        String method = request.getMethod();

        if (method.equals(Method.GET.getVerb())) {
            if (path.equals("/api/media")) {
                return getAllMedia(request);
            }
            if (path.startsWith("/api/media/search")) {
                return searchMedia(request);
            }
            if (path.matches("/api/media/[^/]+")) {
                String id = extractId(path);
                return getMediaById(id);
            }
        }

        if (method.equals(Method.POST.getVerb()) && path.equals("/api/media")) {
            return createMedia(request);
        }

        if (method.equals(Method.PUT.getVerb()) && path.matches("/api/media/[^/]+")) {
            String id = extractId(path);
            return updateMedia(id, request);
        }

        if (method.equals(Method.DELETE.getVerb()) && path.matches("/api/media/[^/]+")) {
            String id = extractId(path);
            return deleteMedia(id, request);
        }

        return status(Status.NOT_FOUND);
    }

    private Response getAllMedia(Request request) {
        String genre = request.getQueryParam("genre");
        String mediaTypeStr = request.getQueryParam("type");
        String yearStr = request.getQueryParam("year");
        String ageStr = request.getQueryParam("age");
        String ratingStr = request.getQueryParam("rating");
        String sortBy = request.getQueryParam("sort");

        MediaType mediaType = null;
        if (mediaTypeStr != null && !mediaTypeStr.isEmpty()) {
            try {
                mediaType = MediaType.valueOf(mediaTypeStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Invalid media type, ignore
            }
        }

        Integer year = parseInteger(yearStr);
        Integer age = parseInteger(ageStr);
        Double rating = parseDouble(ratingStr);

        List<Media> mediaList = mediaService.filterMedia(genre, mediaType, year, age, rating);
        mediaList = mediaService.sortMedia(mediaList, sortBy);

        return json(mediaList, Status.OK);
    }

    private Response getMediaById(String id) {
        Media media = mediaService.getMediaById(id);
        return json(media, Status.OK);
    }

    private Response searchMedia(Request request) {
        String title = request.getQueryParam("title");
        if (title == null || title.isEmpty()) {
            return json(List.of(), Status.OK);
        }

        List<Media> results = mediaService.searchByTitle(title);
        return json(results, Status.OK);
    }

    private Response createMedia(Request request) {
        String userId = RequestContext.getCurrentUserId();
        Media media = toObject(request.getBody(), Media.class);
        Media created = mediaService.createMedia(media, userId);
        return json(created, Status.CREATED);
    }

    private Response updateMedia(String id, Request request) {
        String userId = RequestContext.getCurrentUserId();
        Media media = toObject(request.getBody(), Media.class);
        Media updated = mediaService.updateMedia(id, media, userId);
        return json(updated, Status.OK);
    }

    private Response deleteMedia(String id, Request request) {
        String userId = RequestContext.getCurrentUserId();
        mediaService.deleteMedia(id, userId);
        return status(Status.OK);
    }

    private String extractId(String path) {
        String[] parts = path.split("/");
        return parts[3];
    }

    private Integer parseInteger(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Double parseDouble(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}