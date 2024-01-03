package com.backendoori.ootw.weather.dto;

import java.time.LocalDateTime;

public record WeatherResponse(
    LocalDateTime currentDateTime,
    Double currentTemperature,
    Integer skyCode,
    Integer ptyCode,
    Integer nx,
    Integer ny
) {

}
