package com.backendoori.ootw.util.provider;

import static com.backendoori.ootw.util.provider.ForecastApiCommonRequestSourceProvider.FCST_DATETIME;
import static com.backendoori.ootw.util.provider.ForecastApiCommonRequestSourceProvider.ULTRA_SHORT_FORECAST_BASE_DATETIME;
import static com.backendoori.ootw.util.provider.ForecastApiCommonRequestSourceProvider.VALID_NX;
import static com.backendoori.ootw.util.provider.ForecastApiCommonRequestSourceProvider.VALID_NY;
import static com.backendoori.ootw.util.provider.ForecastApiUltraShortResponseSourceProvider.T1H_VALUE;

import com.backendoori.ootw.weather.domain.forecast.ForecastCategory;
import com.backendoori.ootw.weather.util.deserializer.ForecastResultItem;

public class ForecastApiCommonResponseSourceProvider {

    public static final String NO_DATA_FORECAST_RESPONSE =
        "{\"response\":{\"header\":{\"resultCode\":\"03\",\"resultMsg\":\"NO_DATA\"}}}";
    public static final String INVALID_PARAMETER_FORECAST_RESPONSE =
        "{\"response\":{\"header\":{\"resultCode\":\"10\",\"resultMsg\":\"파라미터가 잘못되엇습니다.\"}}}";
    public static final ForecastResultItem FORECAST_RESPONSE_WITH_ONE_ITEM = new ForecastResultItem(
        ULTRA_SHORT_FORECAST_BASE_DATETIME,
        FCST_DATETIME,
        ForecastCategory.T1H.name(),
        T1H_VALUE,
        VALID_NX,
        VALID_NY
    );
    public static final String ULTRA_SHORT_FORECAST_RESPONSE_WITH_ONE_ITEM_RESPONSE =
        "{\"response\":{\"header\":{\"resultCode\":\"00\",\"resultMsg\":\"NORMAL_SERVICE\"},\"body\":{\"items\":{\"item\":[{\"baseDate\":\"20240110\",\"baseTime\":\"1305\",\"category\":\"T1H\",\"fcstDate\":\"20240110\",\"fcstTime\":\"1400\",\"fcstValue\":\"0.0\",\"nx\":\"50\",\"ny\":\"127\"}]}}}}";
    public static final String VILLAGE_FORECAST_RESPONSE_WITH_ONE_ITEM_RESPONSE =
        "{\"response\":{\"header\":{\"resultCode\":\"00\",\"resultMsg\":\"NORMAL_SERVICE\"},\"body\":{\"items\":{\"item\":[{\"baseDate\":\"20240110\",\"baseTime\":\"1305\",\"category\":\"T1H\",\"fcstDate\":\"20240110\",\"fcstTime\":\"1400\",\"fcstValue\":\"0.0\",\"nx\":\"50\",\"ny\":\"127\"}]}}}}";
    public static final String INVALID_FORECAST_RESPONSE = "INVALID_FORECAST_RESPONSE";

}
