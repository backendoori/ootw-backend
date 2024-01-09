package com.backendoori.ootw.weather.dto;

import java.time.LocalDateTime;
import java.util.Map;
import com.backendoori.ootw.weather.domain.PtyType;
import com.backendoori.ootw.weather.domain.SkyType;
import com.backendoori.ootw.weather.domain.forecast.ForecastCategory;
import com.backendoori.ootw.weather.exception.ForecastResultErrorManager;
import org.springframework.util.Assert;

public record WeatherResponse(
    LocalDateTime currentDateTime,
    double currentTemperature,
    String sky,
    String pty,
    int nx,
    int ny
) {

    public static WeatherResponse from(LocalDateTime dateTime, int nx, int ny,
                                       Map<ForecastCategory, String> currentWeather) {
        Assert.isTrue(
            currentWeather.containsKey(ForecastCategory.T1H)
                && currentWeather.containsKey(ForecastCategory.SKY)
                && currentWeather.containsKey(ForecastCategory.PTY),
            () -> {
                throw ForecastResultErrorManager.getApiServerException();
            });

        double currentTemperature = Double.parseDouble(currentWeather.get(ForecastCategory.T1H));

        int skyCode = Integer.parseInt(currentWeather.get(ForecastCategory.SKY));
        String sky = SkyType.getByCode(skyCode).name();

        int ptyCode = Integer.parseInt(currentWeather.get(ForecastCategory.PTY));
        String pty = PtyType.getByCode(ptyCode).name();

        return new WeatherResponse(dateTime, currentTemperature, sky, pty, nx, ny);
    }

}
