package com.backendoori.ootw.util.provider;

import static com.backendoori.ootw.util.provider.ForecastApiCommonRequestSourceProvider.FCST_DATETIME;
import static com.backendoori.ootw.util.provider.ForecastApiCommonRequestSourceProvider.ULTRA_SHORT_FORECAST_BASE_DATETIME;
import static com.backendoori.ootw.util.provider.ForecastApiCommonRequestSourceProvider.VALID_NX;
import static com.backendoori.ootw.util.provider.ForecastApiCommonRequestSourceProvider.VALID_NY;

import java.util.List;
import com.backendoori.ootw.weather.domain.forecast.ForecastCategory;
import com.backendoori.ootw.weather.util.deserializer.ForecastResultItem;

public class ForecastApiCommonResponseSourceProvider {

    public static final String NO_DATA_FORECAST_RESPONSE =
        "{\"response\":{\"header\":{\"resultCode\":\"03\",\"resultMsg\":\"NO_DATA\"}}}";
    public static final String INVALID_PARAMETER_FORECAST_RESPONSE =
        "{\"response\":{\"header\":{\"resultCode\":\"10\",\"resultMsg\":\"파라미터가 잘못되엇습니다.\"}}}";
    public static final List<ForecastResultItem> INVALID_FORECAST_RESPONSE = List.of(
        new ForecastResultItem(
            ULTRA_SHORT_FORECAST_BASE_DATETIME,
            FCST_DATETIME,
            ForecastCategory.T1H.name(),
            "0.0",
            VALID_NX,
            VALID_NY)
    );

}
