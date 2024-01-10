package com.backendoori.ootw.util.provider;

import static com.backendoori.ootw.util.provider.ForecastApiCommonRequestSourceProvider.DATETIME;
import static com.backendoori.ootw.util.provider.ForecastApiCommonRequestSourceProvider.FCST_DATETIME;
import static com.backendoori.ootw.util.provider.ForecastApiCommonRequestSourceProvider.ULTRA_SHORT_FORECAST_BASE_DATETIME;
import static com.backendoori.ootw.util.provider.ForecastApiCommonRequestSourceProvider.VALID_NX;
import static com.backendoori.ootw.util.provider.ForecastApiCommonRequestSourceProvider.VALID_NY;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.backendoori.ootw.weather.domain.forecast.ForecastCategory;
import com.backendoori.ootw.weather.dto.WeatherResponse;
import com.backendoori.ootw.weather.util.deserializer.ForecastResultItem;

public class ForecastApiUltraShortResponseSourceProvider {

    public static final String T1H_VALUE = "0.0";
    public static final String SKY_VALUE = "1";
    public static final String PTY_VALUE = "1";
    public static final List<ForecastResultItem> VALID_ULTRA_SHORT_FORECAST_ITEMS = List.of(
        new ForecastResultItem(
            ULTRA_SHORT_FORECAST_BASE_DATETIME,
            FCST_DATETIME,
            ForecastCategory.T1H.name(),
            T1H_VALUE,
            VALID_NX,
            VALID_NY),
        new ForecastResultItem(
            ULTRA_SHORT_FORECAST_BASE_DATETIME,
            FCST_DATETIME,
            ForecastCategory.SKY.name(),
            SKY_VALUE,
            VALID_NX,
            VALID_NY),
        new ForecastResultItem(
            ULTRA_SHORT_FORECAST_BASE_DATETIME,
            FCST_DATETIME,
            ForecastCategory.PTY.name(),
            PTY_VALUE,
            VALID_NX,
            VALID_NY)
    );

    public static Map<ForecastCategory, String> generateUltraShortWeatherInfoMap() {
        Map<ForecastCategory, String> weatherInfoMap = new HashMap<>();
        weatherInfoMap.put(ForecastCategory.T1H, T1H_VALUE);
        weatherInfoMap.put(ForecastCategory.SKY, SKY_VALUE);
        weatherInfoMap.put(ForecastCategory.PTY, PTY_VALUE);

        return weatherInfoMap;
    }

    public static WeatherResponse generateWeatherResponse() {
        Map<ForecastCategory, String> weatherInfoMap = new HashMap<>();
        weatherInfoMap.put(ForecastCategory.T1H, T1H_VALUE);
        weatherInfoMap.put(ForecastCategory.SKY, SKY_VALUE);
        weatherInfoMap.put(ForecastCategory.PTY, PTY_VALUE);

        return WeatherResponse.from(DATETIME, VALID_NX, VALID_NY, weatherInfoMap);
    }

}
