package at.technikum.application.mrp;

import at.technikum.application.common.Application;
import at.technikum.application.common.ConnectionPool;
import at.technikum.application.common.Controller;
import at.technikum.application.common.Router;
import at.technikum.application.mrp.controller.*;
import at.technikum.application.mrp.exception.*;
import at.technikum.application.mrp.middleware.AuthMiddleware;
import at.technikum.application.mrp.middleware.RequestContext;
import at.technikum.application.mrp.model.User;
import at.technikum.application.mrp.repository.*;
import at.technikum.application.mrp.service.*;
import at.technikum.application.todo.exception.EntityNotFoundException;
import at.technikum.application.todo.exception.ExceptionMapper;
import at.technikum.application.todo.exception.JsonConversionException;
import at.technikum.application.todo.exception.NotJsonBodyException;
import at.technikum.application.todo.exception.RouteNotFoundException;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;
import at.technikum.server.http.Status;

public class MRPApplication implements Application {
    private final Router router;
    private final ExceptionMapper exceptionMapper;
    private final ConnectionPool connectionPool;
    private final AuthMiddleware authMiddleware;

    private final UserRepository userRepository;
    private final MediaRepository mediaRepository;
    private final RatingRepository ratingRepository;
    private final FavoriteRepository favoriteRepository;
    private final LikeRepository likeRepository;

    private final UserService userService;
    private final MediaService mediaService;
    private final RatingService ratingService;
    private final FavoriteService favoriteService;
    private final RecommendationService recommendationService;

    private final UserController userController;
    private final MediaController mediaController;
    private final RatingController ratingController;
    private final FavoriteController favoriteController;
    private final RecommendationController recommendationController;

    public MRPApplication() {
        this.router = new Router();

        this.connectionPool = new ConnectionPool(
                "postgresql",
                "localhost",
                5432,
                "swen1user",
                "swen1db",
                "mrpdb"
        );

        this.userRepository = new DbUserRepository(connectionPool);
        this.mediaRepository = new DbMediaRepository(connectionPool);
        this.ratingRepository = new DbRatingRepository(connectionPool);
        this.favoriteRepository = new DbFavoriteRepository(connectionPool);
        this.likeRepository = new DbLikeRepository(connectionPool);

        this.mediaService = new MediaService(mediaRepository);
        this.userService = new UserService(userRepository);
        this.ratingService = new RatingService(ratingRepository, mediaService);
        this.favoriteService = new FavoriteService(favoriteRepository, mediaService);
        this.recommendationService = new RecommendationService(mediaRepository, ratingRepository);

        this.authMiddleware = new AuthMiddleware(userService);

        this.userController = new UserController(userService, ratingService);
        this.mediaController = new MediaController(mediaService);
        this.ratingController = new RatingController(ratingService);
        this.favoriteController = new FavoriteController(favoriteService);
        this.recommendationController = new RecommendationController(recommendationService);

        router.addRoute("/api/users", userController);
        router.addRoute("/api/media", mediaController);
        router.addRoute("/api/ratings", ratingController);
        router.addRoute("/api/favorites", favoriteController);
        router.addRoute("/api/recommendations", recommendationController);

        this.exceptionMapper = new ExceptionMapper();
        this.exceptionMapper.register(EntityNotFoundException.class, Status.NOT_FOUND);
        this.exceptionMapper.register(NotJsonBodyException.class, Status.BAD_REQUEST);
        this.exceptionMapper.register(JsonConversionException.class, Status.INTERNAL_SERVER_ERROR);
        this.exceptionMapper.register(RouteNotFoundException.class, Status.NOT_FOUND);
        this.exceptionMapper.register(UnauthorizedException.class, Status.UNAUTHORIZED);
        this.exceptionMapper.register(UserAlreadyExistsException.class, Status.BAD_REQUEST);
        this.exceptionMapper.register(ForbiddenException.class, Status.BAD_REQUEST);
        this.exceptionMapper.register(InvalidRatingException.class, Status.BAD_REQUEST);
        this.exceptionMapper.register(AlreadyExistsException.class, Status.BAD_REQUEST);
    }

    @Override
    public Response handle(Request request) {
        try {
            User authenticatedUser = authMiddleware.authenticate(request);

            // Store user in thread-local for access in controllers and services
            if (authenticatedUser != null) {
                RequestContext.setCurrentUser(authenticatedUser);
                System.out.println("DEBUG: Authenticated user: " + authenticatedUser.getUsername() + " (ID: " + authenticatedUser.getId() + ")");
            } else {
                System.out.println("DEBUG: Public endpoint, no authentication required");
            }

            Controller controller = router.findController(request.getPath())
                    .orElseThrow(() -> new RouteNotFoundException(request.getPath() + " not found"));

            Response response = controller.handle(request);

            // Clean up thread-local
            RequestContext.clear();

            return response;
        } catch (Exception ex) {
            System.err.println("ERROR: " + ex.getClass().getName() + ": " + ex.getMessage());
            ex.printStackTrace();
            RequestContext.clear();
            return exceptionMapper.toResponse(ex);
        }
    }
}