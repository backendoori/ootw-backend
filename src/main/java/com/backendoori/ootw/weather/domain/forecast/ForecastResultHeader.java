package com.backendoori.ootw.weather.domain.forecast;

import com.backendoori.ootw.weather.util.deserializer.ForecastResultHeaderDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = ForecastResultHeaderDeserializer.class)
public record ForecastResultHeader(
    String resultCode,
    String resultMsg
) {

}
