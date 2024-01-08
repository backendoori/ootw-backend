package com.backendoori.ootw.weather.util.deserializer;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = ForecastResultHeaderDeserializer.class)
public record ForecastResultHeader(
    String resultCode,
    String resultMsg
) {

}
