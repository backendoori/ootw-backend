package com.backendoori.ootw.weather.util;

import java.time.LocalDateTime;
import com.backendoori.ootw.weather.dto.forecast.BaseDateTime;

public class BaseDateTimeCalculator {

    private static final int TODAY_TEMPERATURE_ARRANGE_ANNOUNCEMENT_TIME = 2;
    private static final int YESTERDAY_TEMPERATURE_ARRANGE_ANNOUNCEMENT_TIME = 23;

    public static BaseDateTime getCurrentBaseDateTime(LocalDateTime dateTime) {
        String currentDate = BaseDateTimeFormatter.formatBaseDate(dateTime);
        String currentHour = BaseDateTimeFormatter.formatBaseHour(dateTime);

        return new BaseDateTime(currentDate, currentHour);
    }

    public static BaseDateTime getUltraShortForecastRequestBaseDateTime(LocalDateTime dateTime) {
        String baseRequestDate = BaseDateTimeFormatter.formatBaseDate(dateTime);
        String baseRequestTime = BaseDateTimeFormatter.formatBaseTime(dateTime.minusHours(1));
        if (dateTime.getHour() == 0) {
            baseRequestDate = BaseDateTimeFormatter.formatBaseDate(dateTime.minusDays(1));
            baseRequestTime = BaseDateTimeFormatter.formatBaseTime(dateTime.withHour(23));
        }

        return new BaseDateTime(baseRequestDate, baseRequestTime);
    }

    public static BaseDateTime getVillageForecastRequestBaseDateTime(LocalDateTime dateTime) {
        String baseRequestDate = BaseDateTimeFormatter.formatBaseDate(dateTime);
        String baseRequestTime = BaseDateTimeFormatter.formatBaseTime(
            dateTime.withHour(TODAY_TEMPERATURE_ARRANGE_ANNOUNCEMENT_TIME));

        if (dateTime.getHour() < 3) {
            baseRequestDate = BaseDateTimeFormatter.formatBaseDate(dateTime.minusDays(1));
            baseRequestTime = BaseDateTimeFormatter.formatBaseTime(
                dateTime.withHour(YESTERDAY_TEMPERATURE_ARRANGE_ANNOUNCEMENT_TIME));
        }

        return new BaseDateTime(baseRequestDate, baseRequestTime);
    }

}
