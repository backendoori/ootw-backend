package com.backendoori.ootw.weather.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import com.backendoori.ootw.weather.domain.TemperatureArrange;
import com.backendoori.ootw.weather.domain.forecast.ForecastCategory;
import com.backendoori.ootw.weather.dto.WeatherResponse;
import com.backendoori.ootw.weather.dto.forecast.BaseDateTime;
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
        BaseDateTime requestBaseDateTime = BaseDateTimeCalculator.getUltraShortForecastRequestBaseDateTime(dateTime);
        BaseDateTime fcstBaseDateTime = BaseDateTimeCalculator.getCurrentBaseDateTime(dateTime);

        Map<ForecastCategory, String> currentWeather = new HashMap<>();
        forecastApiClient.requestUltraShortForecastItems(requestBaseDateTime, nx, ny)
            .stream()
            .filter(item -> item.matchFcstDateTime(fcstBaseDateTime))
            .forEach(item -> currentWeather.put(ForecastCategory.valueOf(item.category()), item.fcstValue()));

        return WeatherResponse.from(dateTime, nx, ny, currentWeather);
    }

    public TemperatureArrange getCurrentTemperatureArrange(int nx, int ny) {
        LocalDateTime dateTime = LocalDateTime.now();
        BaseDateTime requestBaseDateTime = BaseDateTimeCalculator.getVilageForecastRequestBaseDateTime(dateTime);
        BaseDateTime fcstBaseDateTime = BaseDateTimeCalculator.getCurrentBaseDateTime(dateTime);

        Map<ForecastCategory, String> temperatureArrangeMap = new HashMap<>();
        forecastApiClient.requestVillageForecastItems(requestBaseDateTime, nx, ny)
            .stream()
            .filter(item -> item.fcstDateTime().baseDate().equals(fcstBaseDateTime.baseDate()))
            .filter(item -> item.category().equals(ForecastCategory.TMN.name())
                || item.category().equals(ForecastCategory.TMX.name()))
            .forEach(item -> temperatureArrangeMap.put(ForecastCategory.valueOf(item.category()), item.fcstValue()));

        return TemperatureArrange.from(temperatureArrangeMap);
    }

}
