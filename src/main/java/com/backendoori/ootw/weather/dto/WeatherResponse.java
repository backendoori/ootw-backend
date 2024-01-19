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
    int nx,
    int ny,
    double currentTemperature,
    String sky,
    String pty
) {

    public static WeatherResponse from(LocalDateTime dateTime, int nx, int ny,
                                       Map<ForecastCategory, String> weatherInfoMap) {
        checkIncludeCurrentWeather(weatherInfoMap);

        double currentTemperature = Double.parseDouble(weatherInfoMap.get(ForecastCategory.T1H));

        int skyCode = Integer.parseInt(weatherInfoMap.get(ForecastCategory.SKY));
        String skyType = SkyType.getByCode(skyCode).name();

        int ptyCode = Integer.parseInt(weatherInfoMap.get(ForecastCategory.PTY));
        String ptyType = PtyType.getByCode(ptyCode).name();

        return new WeatherResponse(dateTime, nx, ny, currentTemperature, skyType, ptyType);
    }

    private static void checkIncludeCurrentWeather(Map<ForecastCategory, String> currentWeatherMap) {
        Assert.isTrue(
            currentWeatherMap.containsKey(ForecastCategory.T1H)
                && currentWeatherMap.containsKey(ForecastCategory.SKY)
                && currentWeatherMap.containsKey(ForecastCategory.PTY),
            () -> {
                throw ForecastResultErrorManager.getApiServerException();
            });
    }

}
