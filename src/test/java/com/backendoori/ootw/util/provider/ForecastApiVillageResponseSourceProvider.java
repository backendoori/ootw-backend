package com.backendoori.ootw.util.provider;

import static com.backendoori.ootw.util.provider.ForecastApiCommonRequestSourceProvider.FCST_DATETIME;
import static com.backendoori.ootw.util.provider.ForecastApiCommonRequestSourceProvider.VALID_NX;
import static com.backendoori.ootw.util.provider.ForecastApiCommonRequestSourceProvider.VALID_NY;
import static com.backendoori.ootw.util.provider.ForecastApiCommonRequestSourceProvider.VILLAGE_FORECAST_BASE_DATETIME;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.backendoori.ootw.weather.domain.forecast.ForecastCategory;
import com.backendoori.ootw.weather.util.deserializer.ForecastResultItem;

public class ForecastApiVillageResponseSourceProvider {

    public static final String TMX_VALUE = "1.0";
    public static final String TMN_VALUE = "-1.0";

    public static final List<ForecastResultItem> VALID_VILLAGE_FORECAST_ITEMS = List.of(
        new ForecastResultItem(
            VILLAGE_FORECAST_BASE_DATETIME,
            FCST_DATETIME,
            ForecastCategory.TMN.name(),
            TMN_VALUE,
            VALID_NX,
            VALID_NY),
        new ForecastResultItem(
            VILLAGE_FORECAST_BASE_DATETIME,
            FCST_DATETIME,
            ForecastCategory.TMX.name(),
            TMX_VALUE,
            VALID_NX,
            VALID_NY)
    );

    public static Map<ForecastCategory, String> generateVillageWeatherInfoMap() {
        Map<ForecastCategory, String> weatherInfoMap = new HashMap<>();
        weatherInfoMap.put(ForecastCategory.TMN, TMN_VALUE);
        weatherInfoMap.put(ForecastCategory.TMX, TMX_VALUE);

        return weatherInfoMap;
    }

}
