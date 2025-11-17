package at.technikum.application.weather.controller;

import at.technikum.application.common.Controller;
import at.technikum.application.weather.model.Weather;
import at.technikum.application.weather.service.WeatherService;
import at.technikum.server.http.ContentType;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;
import at.technikum.server.http.Status;

public class CityController extends Controller {

    private final WeatherService weatherService;

    public CityController() {
        this.weatherService = new WeatherService();
    }

    @Override
    public Response handle(Request request) {
        if (request.getMethod().equals("GET")) {
            return readWeather(request);
        }

        // TODO
        throw new RuntimeException("404");
    }

    private Response readWeather(Request request) {

        String city = request.getPath().split("/")[2];
        Weather weather = weatherService.getByCity(city);

        Response response = new Response();
        response.setStatus(Status.OK);
        response.setContentType(ContentType.TEXT_PLAIN);
        response.setBody(String.valueOf(weather.getTemperature()));

        return response;
    }
}
