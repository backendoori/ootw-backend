package com.backendoori.ootw.weather.dto.forecast;

public record ForecastResultItem(

    String baseDate,
    String baseTime,
    String category,
    String fcstDate,
    String fcstTime,
    String fcstValue,
    int nx,
    int ny
) {

}
