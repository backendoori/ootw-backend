package com.backendoori.ootw.weather.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BaseDateTimeFormatter {

    private static final String BASE_DATE_FORMAT = "yyyyMMdd";
    private static final String BASE_TIME_FORMAT = "HHmm";
    private static final String BASE_HOUR_FORMAT = "HH00";

    public static String formatBaseDate(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern(BASE_DATE_FORMAT));
    }

    public static String formatBaseTime(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern(BASE_TIME_FORMAT));
    }

    public static String formatBaseHour(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern(BASE_HOUR_FORMAT));
    }

}
