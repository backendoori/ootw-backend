package com.backendoori.ootw.weather.util.client;

import static com.backendoori.ootw.weather.validation.Message.INVALID_LOCATION_MESSAGE;

import java.util.List;
import com.backendoori.ootw.weather.domain.Coordinate;
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

    private final ForecastApi forecastApi;
    private final ObjectMapper objectMapper;
    private final ForecastProperties forecastProperties;

    public List<ForecastResultItem> requestUltraShortForecastItems(BaseDateTime requestBaseDateTime,
                                                                   Coordinate location) {
        validateLocation(location);

        String response = forecastApi.getUltraShortForecast(
            forecastProperties.serviceKey(),
            NUM_OF_ROWS,
            PAGE_NO,
            DATA_TYPE,
            requestBaseDateTime.baseDate(),
            requestBaseDateTime.baseTime(),
            location.nx(),
            location.ny());

        return parseForecastResult(response);
    }

    public List<ForecastResultItem> requestVillageForecastItems(BaseDateTime requestBaseDateTime,
                                                                Coordinate location) {
        validateLocation(location);

        String response = forecastApi.getVillageForecast(
            forecastProperties.serviceKey(),
            NUM_OF_ROWS,
            PAGE_NO,
            DATA_TYPE,
            requestBaseDateTime.baseDate(),
            requestBaseDateTime.baseTime(),
            location.nx(),
            location.ny());

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

    private void validateLocation(Coordinate location) {
        Assert.isTrue(0 <= location.nx() && location.nx() <= 999 && 0 <= location.ny() && location.ny() <= 999, () -> {
            throw new IllegalArgumentException(INVALID_LOCATION_MESSAGE);
        });
    }

}
