package com.backendoori.ootw.weather.util;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "openfeign.forecast")
public record ForecastProperties(
    String serviceKey
) {

}
