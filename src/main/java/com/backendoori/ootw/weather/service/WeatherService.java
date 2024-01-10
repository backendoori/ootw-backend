package com.backendoori.ootw.weather.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import com.backendoori.ootw.weather.domain.Coordinate;
import com.backendoori.ootw.weather.domain.TemperatureArrange;
import com.backendoori.ootw.weather.domain.forecast.ForecastCategory;
import com.backendoori.ootw.weather.dto.WeatherResponse;
import com.backendoori.ootw.weather.dto.forecast.BaseDateTime;
import com.backendoori.ootw.weather.util.BaseDateTimeCalculator;
import com.backendoori.ootw.weather.util.DateTimeProvider;
import com.backendoori.ootw.weather.util.client.ForecastApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WeatherService {

    private final DateTimeProvider dateTimeProvider;
    private final ForecastApiClient forecastApiClient;

    public WeatherResponse getCurrentWeather(Coordinate location) {
        LocalDateTime dateTime = dateTimeProvider.now();
        BaseDateTime requestBaseDateTime = BaseDateTimeCalculator.getUltraShortForecastRequestBaseDateTime(dateTime);
        BaseDateTime fcstBaseDateTime = BaseDateTimeCalculator.getCurrentBaseDateTime(dateTime);

        Map<ForecastCategory, String> weatherInfoMap = new HashMap<>();
        forecastApiClient.requestUltraShortForecastItems(requestBaseDateTime, location)
            .stream()
            .filter(item -> item.matchFcstDateTime(fcstBaseDateTime))
            .forEach(item -> weatherInfoMap.put(ForecastCategory.valueOf(item.category()), item.fcstValue()));

        return WeatherResponse.from(dateTime, location, weatherInfoMap);
    }

    public TemperatureArrange getCurrentTemperatureArrange(Coordinate location) {
        LocalDateTime dateTime = dateTimeProvider.now();
        BaseDateTime requestBaseDateTime = BaseDateTimeCalculator.getVillageForecastRequestBaseDateTime(dateTime);
        BaseDateTime fcstBaseDateTime = BaseDateTimeCalculator.getCurrentBaseDateTime(dateTime);

        Map<ForecastCategory, String> weatherInfoMap = new HashMap<>();
        forecastApiClient.requestVillageForecastItems(requestBaseDateTime, location)
            .stream()
            .filter(item -> item.matchFcstDate(fcstBaseDateTime))
            .forEach(item -> weatherInfoMap.put(ForecastCategory.valueOf(item.category()), item.fcstValue()));

        return TemperatureArrange.from(weatherInfoMap);
    }

}
