package com.backendoori.ootw.dto;

import com.backendoori.ootw.domain.weather.Weather;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record WeatherInfo(
    @NotNull
    @DecimalMin(value = "-900.0", inclusive = false)
    @DecimalMax(value = "900.0", inclusive = false)
    Double currentTemperature,

    @NotNull
    @DecimalMin(value = "-900.0", inclusive = false)
    @DecimalMax(value = "900.0", inclusive = false)
    Double dayMinTemperature,

    @NotNull
    @DecimalMin(value = "-900.0", inclusive = false)
    @DecimalMax(value = "900.0", inclusive = false)
    Double dayMaxTemperature,

    @NotNull
    @Positive
    Integer skyCode,

    @NotNull
    @PositiveOrZero
    Integer ptyCode
) {

    public static WeatherInfo from(Weather weather) {
        return new WeatherInfo(
            weather.getCurrentTemperature().getValue(),
            weather.getDayMinTemperature().getValue(),
            weather.getDayMaxTemperature().getValue(),
            weather.getSkyType().getCode(),
            weather.getPtyType().getCode()
        );
    }

}
