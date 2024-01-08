package com.backendoori.ootw.weather.util.deserializer;

import java.util.List;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = ForecastSuccessResultBodyDeserializer.class)
public record ForecastSuccessResultBody(
    List<ForecastResultItem> items
) {

}
