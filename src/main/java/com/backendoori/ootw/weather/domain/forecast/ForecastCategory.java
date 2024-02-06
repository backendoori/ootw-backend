package com.backendoori.ootw.weather.domain.forecast;

import java.util.Arrays;
import java.util.Objects;

public enum ForecastCategory {

    TMX, PTY, SKY, TMN, T1H;

    public static boolean anyMatch(String categoryName) {
        return Arrays.stream(values()).anyMatch(category -> Objects.equals(categoryName, category.name()));
    }

}
