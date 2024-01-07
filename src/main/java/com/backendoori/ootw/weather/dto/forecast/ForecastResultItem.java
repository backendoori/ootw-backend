package com.backendoori.ootw.weather.dto.forecast;

import java.util.Objects;
import com.backendoori.ootw.weather.util.deserializer.ForecastResultItemSerializer;
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

    public boolean matchFcstDateTimeWithBaseDateTime(BaseDateTime baseDateTime) {
        return Objects.equals(fcstDateTime, baseDateTime);
    }

}
