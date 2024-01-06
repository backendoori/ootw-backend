package com.backendoori.ootw.weather.dto;

import java.time.LocalDateTime;
import java.util.Map;
import com.backendoori.ootw.weather.domain.PtyType;
import com.backendoori.ootw.weather.domain.SkyType;
import com.backendoori.ootw.weather.domain.forecast.ForecastCategory;

public record WeatherResponse(
    LocalDateTime currentDateTime,
    Double currentTemperature,
    String sky,
    String pty,
    Integer nx,
    Integer ny
) {

    public static WeatherResponse from(LocalDateTime dateTime, Integer nx, Integer ny,
                                       Map<ForecastCategory, String> currentWeather) {

        Double currentTemperature = Double.valueOf(currentWeather.get(ForecastCategory.T1H));

        Integer skyCode = Integer.valueOf(currentWeather.get(ForecastCategory.SKY));
        String sky = SkyType.getByCode(skyCode).name();

        Integer ptyCode = Integer.valueOf(currentWeather.get(ForecastCategory.PTY));
        String pty = PtyType.getByCode(ptyCode).name();

        return new WeatherResponse(dateTime, currentTemperature, sky, pty, nx, ny);
    }

}
