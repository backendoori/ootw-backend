package com.backendoori.ootw.weather.util.client;

import java.util.List;
import com.backendoori.ootw.weather.dto.forecast.BaseDateTime;
import com.backendoori.ootw.weather.exception.ForecastResultErrorManager;
import com.backendoori.ootw.weather.util.ForecastProperties;
import com.backendoori.ootw.weather.util.deserializer.ForecastResultHeader;
import com.backendoori.ootw.weather.util.deserializer.ForecastResultItem;
import com.backendoori.ootw.weather.util.deserializer.ForecastSuccessResultBody;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@RequiredArgsConstructor
public class ForecastApiClient {

    private static final int NUM_OF_ROWS = 500;
    private static final int PAGE_NO = 1;
    private static final String DATA_TYPE = "JSON";
    private static final String INVALID_LOCATION_MESSAGE = "위치값이 유효하지 않습니다.";

    private final ForecastApi forecastApi;
    private final ObjectMapper objectMapper;
    private final ForecastProperties forecastProperties;

    public List<ForecastResultItem> requestUltraShortForecastItems(BaseDateTime requestBaseDateTime,
                                                                   int nx, int ny) {
        validateLocation(nx, ny);

        String response = forecastApi.getUltraShortForecast(
            forecastProperties.serviceKey(),
            NUM_OF_ROWS,
            PAGE_NO,
            DATA_TYPE,
            requestBaseDateTime.baseDate(),
            requestBaseDateTime.baseTime(),
            nx,
            ny);

        return parseForecastResult(response);
    }

    public List<ForecastResultItem> requestVillageForecastItems(BaseDateTime requestBaseDateTime,
                                                                int nx, int ny) {
        validateLocation(nx, ny);

        String response = forecastApi.getVillageForecast(
            forecastProperties.serviceKey(),
            NUM_OF_ROWS,
            PAGE_NO,
            DATA_TYPE,
            requestBaseDateTime.baseDate(),
            requestBaseDateTime.baseTime(),
            nx,
            ny);

        return parseForecastResult(response);
    }

    private List<ForecastResultItem> parseForecastResult(String response) {
        try {
            ForecastResultHeader header = objectMapper.readValue(response, ForecastResultHeader.class);
            ForecastResultErrorManager.checkResultCode(header.resultCode());

            return objectMapper.readValue(response, ForecastSuccessResultBody.class)
                .items();
        } catch (JacksonException e) {
            throw ForecastResultErrorManager.getApiServerException();
        }
    }

    private void validateLocation(int nx, int ny) {
        Assert.isTrue(0 <= nx && nx <= 999 && 0 <= ny && ny <= 999, () -> {
            throw new IllegalArgumentException(INVALID_LOCATION_MESSAGE);
        });
    }

}
