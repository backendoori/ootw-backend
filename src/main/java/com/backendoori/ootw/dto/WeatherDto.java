package com.backendoori.ootw.dto;

import com.backendoori.ootw.domain.weather.Weather;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record WeatherDto(
    @NotNull
    @Min(value = -900)
    @Max(value = 900)
    Double currentTemperature,

    @NotNull
    @Min(value = -900)
    @Max(value = 900)
    Double dayMinTemperature,

    @NotNull
    @Min(value = -900)
    @Max(value = 900)
    Double dayMaxTemperature,

    @NotNull
    @Positive
    Integer skyCode,

    @NotNull
    @PositiveOrZero
    Integer ptyCode
) {

    public static WeatherDto from(Weather weather) {
        return new WeatherDto(
            weather.getCurrentTemperature().getValue(),
            weather.getDayMinTemperature().getValue(),
            weather.getDayMaxTemperature().getValue(),
            weather.getSkyType().getCode(),
            weather.getPtyType().getCode()
        );
    }

}
