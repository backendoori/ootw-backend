package com.backendoori.ootw.weather.dto.forecast;

import java.util.List;
import com.backendoori.ootw.weather.util.deserializer.ForecastSuccessResultBodyDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = ForecastSuccessResultBodyDeserializer.class)
public record ForecastSuccessResultBody(
    List<ForecastResultItem> items
) {

}
