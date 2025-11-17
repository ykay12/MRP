package at.technikum.application.weather.service;

import at.technikum.application.weather.model.Weather;

public class WeatherService {

    public Weather getByCity(String city) {
        if (city.equals("vienna")) {
            return new Weather(city, 22.0f);
        }
        if (city.equals("berlin")) {
            return new Weather(city, 18.0f);
        }

        throw  new RuntimeException("city not found");
    }
}
