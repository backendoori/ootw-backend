package com.backendoori.ootw.weather.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import com.backendoori.ootw.weather.domain.BaseDateTime;
import com.backendoori.ootw.weather.domain.ForecastCategory;
import com.backendoori.ootw.weather.dto.WeatherResponse;
import com.backendoori.ootw.weather.util.BaseDateTimeCalculator;
import com.backendoori.ootw.weather.util.client.ForecastApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WeatherService {

    private final ForecastApiClient forecastApiClient;

    public WeatherResponse getCurrentWeather(int nx, int ny) {
        LocalDateTime dateTime = LocalDateTime.now();
        BaseDateTime requestBaseDateTime = BaseDateTimeCalculator.getRequestBaseDateTime(dateTime);
        BaseDateTime currentBaseDateTime = BaseDateTimeCalculator.getCurrentBaseDateTime(dateTime);

        Map<ForecastCategory, String> currentWeather = new HashMap<>();
        forecastApiClient.requestUltraShortForecastItems(requestBaseDateTime, nx, ny).forEach(
            item -> {
                if (item.matchFcstDateTimeWithBaseDateTime(currentBaseDateTime)) {
                    currentWeather.put(ForecastCategory.valueOf(item.category()), item.fcstValue());
                }
            });

        return WeatherResponse.from(dateTime, nx, ny, currentWeather);
    }

}
