package com.backendoori.ootw.weather.dto;

import com.backendoori.ootw.weather.domain.TemperatureArrange;

public record TemperatureArrangeDto(
    double min,
    double max
) {

    public static TemperatureArrangeDto from(TemperatureArrange weather) {
        return new TemperatureArrangeDto(
            weather.getMin().getValue(),
            weather.getMax().getValue()
        );
    }

}
