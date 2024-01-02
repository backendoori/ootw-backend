package com.backendoori.ootw.weather.util;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "forecast")
public record ForecastProperties(
    String serviceKey
) {

}
