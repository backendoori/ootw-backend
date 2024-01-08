package com.backendoori.ootw.weather.dto;

import com.backendoori.ootw.weather.domain.Weather;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record WeatherDto(

    @NotNull
    @Min(value = -900)
    @Max(value = 900)
    Double dayMinTemperature,

    @NotNull
    @Min(value = -900)
    @Max(value = 900)
    Double dayMaxTemperature
) {

    public static WeatherDto from(Weather weather) {
        return new WeatherDto(
            weather.getDayMinTemperature().getValue(),
            weather.getDayMaxTemperature().getValue()
        );
    }

}
