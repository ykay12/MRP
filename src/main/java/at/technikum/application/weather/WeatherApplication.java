package at.technikum.application.weather;

import at.technikum.application.common.Application;
import at.technikum.application.common.Controller;
import at.technikum.application.common.Router;
import at.technikum.application.weather.controller.CityController;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;

public class WeatherApplication implements Application {

    private final Router router;

    public WeatherApplication() {
        this.router = new Router();

        this.router.addRoute("/city", new CityController());
    }

    @Override
    public Response handle(Request request) {

        if (this.router.findController(request.getPath()).isEmpty()) {
            // return 404
        }

        Controller controller = this.router.findController(request.getPath())
                .orElseThrow(RuntimeException::new);

        return controller.handle(request);
    }
}
