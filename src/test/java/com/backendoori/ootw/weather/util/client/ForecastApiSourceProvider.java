package com.backendoori.ootw.weather.util.client;

import java.time.LocalDateTime;
import java.util.List;
import com.backendoori.ootw.weather.dto.forecast.BaseDateTime;
import com.backendoori.ootw.weather.util.BaseDateTimeCalculator;
import com.backendoori.ootw.weather.util.deserializer.ForecastResultItem;

public class ForecastApiSourceProvider {

    // api request source
    static final Integer VALID_NX = 50;
    static final Integer VALID_NY = 127;
    static final LocalDateTime DATETIME = LocalDateTime.of(2024, 1, 10, 14, 5);
    static final BaseDateTime TEMP_BASE_DATETIME =
        BaseDateTimeCalculator.getUltraShortForecastRequestBaseDateTime(DATETIME);

    // api response source
    static final List<ForecastResultItem> VALID_ULTRA_SHORT_FORECAST_RESPONSE = List.of();
    static final List<ForecastResultItem> VALID_VILLAGE_FORECAST_RESPONSE = List.of();

    static final List<ForecastResultItem> INVALID_ULTRA_SHORT_FORECAST_RESPONSE = List.of();
    static final List<ForecastResultItem> INVALID_VILLAGE_FORECAST_RESPONSE = List.of();

    static final String NO_DATA_FORECAST_RESPONSE =
        "{\"response\":{\"header\":{\"resultCode\":\"03\",\"resultMsg\":\"NO_DATA\"}}}";
    static final String INVALID_PARAMETER_FORECAST_RESPONSE =
        "{\"response\":{\"header\":{\"resultCode\":\"10\",\"resultMsg\":\"파라미터가 잘못되엇습니다.\"}}}";

}
