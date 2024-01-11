package com.backendoori.ootw.weather.util.deserializer;

import java.util.Objects;
import com.backendoori.ootw.weather.dto.forecast.BaseDateTime;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = ForecastResultItemSerializer.class)
public record ForecastResultItem(
    BaseDateTime baseDateTime,
    BaseDateTime fcstDateTime,
    String category,
    String fcstValue,
    int nx,
    int ny
) {

    public boolean matchFcstDateTime(BaseDateTime baseDateTime) {
        return Objects.equals(fcstDateTime, baseDateTime);
    }

    public boolean matchFcstDate(BaseDateTime baseDateTime) {
        return Objects.equals(fcstDateTime.baseDate(), baseDateTime.baseDate());
    }

}
