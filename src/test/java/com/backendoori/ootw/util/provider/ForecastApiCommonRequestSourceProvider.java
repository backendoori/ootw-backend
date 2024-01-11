package com.backendoori.ootw.util.provider;

import java.time.LocalDateTime;
import com.backendoori.ootw.weather.domain.Coordinate;
import com.backendoori.ootw.weather.dto.forecast.BaseDateTime;
import com.backendoori.ootw.weather.util.BaseDateTimeCalculator;

public class ForecastApiCommonRequestSourceProvider {

    public static final Integer VALID_NX = 50;
    public static final Integer VALID_NY = 127;
    public static final Coordinate VALID_COORDINATE = new Coordinate(VALID_NX, VALID_NY);
    public static final Integer NO_DATA_NX = 0;
    public static final Integer NO_DATA_NY = 0;
    public static final Coordinate NO_DATA_COORDINATE = new Coordinate(NO_DATA_NX, NO_DATA_NY);
    public static final LocalDateTime DATETIME = LocalDateTime.of(2024, 1, 10, 14, 5);
    public static final BaseDateTime ULTRA_SHORT_FORECAST_BASE_DATETIME =
        BaseDateTimeCalculator.getUltraShortForecastRequestBaseDateTime(DATETIME);
    public static final BaseDateTime VILLAGE_FORECAST_BASE_DATETIME =
        BaseDateTimeCalculator.getVillageForecastRequestBaseDateTime(DATETIME);
    public static final BaseDateTime FCST_DATETIME =
        BaseDateTimeCalculator.getCurrentBaseDateTime(DATETIME);

}
