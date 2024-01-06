package com.backendoori.ootw.weather.domain.forecast;

import java.util.Arrays;

public enum ForecastCategory {
    TMX, TMP, PTY, SKY, TMN, T1H;

    public static boolean anyMatch(String categoryName) {
        return Arrays.stream(values()).anyMatch(category -> category.name().equals(categoryName));
    }

}
