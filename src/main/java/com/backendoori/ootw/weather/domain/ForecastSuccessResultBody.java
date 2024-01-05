package com.backendoori.ootw.weather.domain;

import java.util.List;
import com.backendoori.ootw.weather.util.deserializer.ForecastResultDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = ForecastResultDeserializer.class)
public record ForecastSuccessResultBody(
    List<ForecastResultItem> items
) {

}
