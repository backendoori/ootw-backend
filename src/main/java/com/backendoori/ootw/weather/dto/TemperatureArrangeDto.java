package com.backendoori.ootw.weather.dto;

import com.backendoori.ootw.weather.domain.TemperatureArrange;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record TemperatureArrangeDto(

    @NotNull
    @Min(value = -900)
    @Max(value = 900)
    Double min,

    @NotNull
    @Min(value = -900)
    @Max(value = 900)
    Double max
) {

    public static TemperatureArrangeDto from(TemperatureArrange weather) {
        return new TemperatureArrangeDto(
            weather.getMin().getValue(),
            weather.getMax().getValue()
        );
    }

}
