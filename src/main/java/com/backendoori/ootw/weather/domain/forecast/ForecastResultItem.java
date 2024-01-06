package com.backendoori.ootw.weather.domain.forecast;

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

    public boolean matchFcstDateTimeWithBaseDateTime(BaseDateTime baseDateTime) {
        return this.fcstDate().equals(baseDateTime.baseDate())
            && this.fcstTime().equals(baseDateTime.baseTime());
    }

}
