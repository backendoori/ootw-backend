package com.backendoori.ootw.weather.util;

import java.time.LocalDateTime;
import com.backendoori.ootw.weather.domain.BaseDateTime;

public class BaseDateTimeCalculator {

    public static BaseDateTime getCurrentBaseDateTime(LocalDateTime dateTime) {
        String currentDate = BaseDateTimeFormatter.formatBaseDate(dateTime);
        String currentHour = BaseDateTimeFormatter.formatBaseHour(dateTime);

        return new BaseDateTime(currentDate, currentHour);
    }

    public static BaseDateTime getRequestBaseDateTime(LocalDateTime dateTime) {
        String baseRequestDate = BaseDateTimeFormatter.formatBaseDate(dateTime);
        String baseRequestTime = BaseDateTimeFormatter.formatBaseTime(dateTime.minusHours(1));
        if (dateTime.getHour() == 0) {
            baseRequestDate = BaseDateTimeFormatter.formatBaseDate(dateTime.minusDays(1));
            baseRequestTime = BaseDateTimeFormatter.formatBaseTime(dateTime.withHour(23));
        }

        return new BaseDateTime(baseRequestDate, baseRequestTime);
    }

}
