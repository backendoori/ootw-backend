package com.backendoori.ootw.weather.dto.forecast;

import java.util.List;
import com.backendoori.ootw.weather.util.deserializer.ForecastResultDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = ForecastResultDeserializer.class)
public record ForecastResult(
    List<ForecastResultItem> items
) {

}
