package com.backendoori.ootw.weather.dto;

import com.backendoori.ootw.weather.domain.TemperatureArrange;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record TemperatureArrangeDto(

    @NotNull
    @Min(value = -900)
    @Max(value = 900)
    Double minTemperature,

    @NotNull
    @Min(value = -900)
    @Max(value = 900)
    Double maxTemperature
) {

    public static TemperatureArrangeDto from(TemperatureArrange weather) {
        return new TemperatureArrangeDto(
            weather.getMinTemperature().getValue(),
            weather.getMaxTemperature().getValue()
        );
    }

}
