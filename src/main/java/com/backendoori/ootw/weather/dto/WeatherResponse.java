package com.backendoori.ootw.weather.dto;

import java.time.LocalDateTime;
import java.util.Map;
import com.backendoori.ootw.weather.domain.ForecastCategory;

public record WeatherResponse(
    LocalDateTime currentDateTime,
    Double currentTemperature,
    Integer skyCode,
    Integer ptyCode,
    Integer nx,
    Integer ny
) {

    public static WeatherResponse from(LocalDateTime dateTime, int nx, int ny,
                                       Map<ForecastCategory, String> currentWeather) {

        Double currentTemperature = Double.valueOf(currentWeather.get(ForecastCategory.T1H));
        Integer skyCode = Integer.valueOf(currentWeather.get(ForecastCategory.SKY));
        Integer ptyCode = Integer.valueOf(currentWeather.get(ForecastCategory.PTY));

        return new WeatherResponse(dateTime, currentTemperature, skyCode, ptyCode, nx, ny);
    }

}
